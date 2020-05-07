package builder;

import creator.AccountCreator;
import creator.CustomerCreator;
import domain.Account;
import domain.Customer;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class CreateCustomerBuilder extends RouteBuilder{

    @Override
    public void configure() throws Exception { 
        
        // Unmarshals the message received into an Account object.
        from("jetty:http://localhost:9000/createaccount?enableCORS=true")
           // make message in-only so web browser doesn't have to wait on a non-existent response
           .setExchangePattern(ExchangePattern.InOnly)
           .log("Account created: ${body}")
           .unmarshal().json(JsonLibrary.Gson, Account.class)
           .log("Send to create-account queue: ${body}")
           .to("jms:queue:create-account");
        
        // Converts the Account object to a Customer object.
        from("jms:queue:create-account")
            .bean(CustomerCreator.class, "createCustomer(${body})")
            .log("Send to vend queue: ${body}")
            .to("jms:queue:vend");
        
        // Sends the JSON Customer object to Vend.
        from("jms:queue:vend")
            .log("Received Customer pre-marshal: ${body}")
            // remove headers so they don't get sent to Vend
            // remove headers so they don't get sent to Vend
            .removeHeaders("*")

            // add authentication token to authorization header
            .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))

            // marshal to JSON
            .marshal().json(JsonLibrary.Gson)  // only necessary if the message is an object, not JSON
            .setHeader(Exchange.CONTENT_TYPE).constant("application/json")

            // set HTTP method
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .log("Send to vend: ${body}")
            .to("https://info303otago.vendhq.com/api/2.0/customers")
            .to("jms:queue:vend-response");     
        
        from("jms:queue:vend-response")
            .log("Vend body: ${body}")
            .setBody().jsonpath("$.data")
            .marshal().json(JsonLibrary.Gson)
            .unmarshal().json(JsonLibrary.Gson, Customer.class)
            .log("Customer with ID: ${body}")
            .bean(AccountCreator.class, "createAccount(${body})")
            .log("New Account with ID: ${body}")
            .marshal().json(JsonLibrary.Gson)
            .to("jms:queue:extracted-response");
        from("jms:queue:extracted-response")
            .removeHeaders("*") // remove headers to stop them being sent to the service
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .to("http://localhost:8086/api/accounts")
            .to("jms:queue:displayed-account");
    }
}
