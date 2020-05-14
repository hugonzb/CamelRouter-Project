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
            .removeHeaders("*")
            .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))
            .marshal().json(JsonLibrary.Gson)  
            .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .log("Send to vend: ${body}")
            .to("https://info303otago.vendhq.com/api/2.0/customers")
            .to("jms:queue:vend-response");     
        
        // Extracts Customer response and converts back to Account object.
        from("jms:queue:vend-response")
            .log("Vend body: ${body}")
            .setBody().jsonpath("$.data")
            .marshal().json(JsonLibrary.Gson)
            .unmarshal().json(JsonLibrary.Gson, Customer.class)
            .log("Customer with ID: ${body}")
            .bean(AccountCreator.class, "createAccount(${body})")
            .log("New Account: ${body}")
            .marshal().json(JsonLibrary.Gson)
            .to("jms:queue:account");
        
        // POSTs the new Account object to the Accounts service.
        from("jms:queue:account")
            .removeHeaders("*")
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .to("http://localhost:8086/api/accounts")
            .to("jms:queue:account-response");
    }
}
