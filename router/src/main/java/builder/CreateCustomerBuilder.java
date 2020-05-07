package builder;

import creator.CustomerCreator;
import domain.Account;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class CreateCustomerBuilder extends RouteBuilder{

    @Override
    public void configure() throws Exception { 
        // create HTTP endpoint for receiving messages via HTTP
        from("jetty:http://localhost:9000/createaccount?enableCORS=true")
           // make message in-only so web browser doesn't have to wait on a non-existent response
           .setExchangePattern(ExchangePattern.InOnly)
           .log("Account created: ${body}")
           .unmarshal().json(JsonLibrary.Gson, Account.class)
           .log("Send to create-account queue: ${body}")
           .to("jms:queue:create-account");
        
        from("jms:queue:create-account")
            .bean(CustomerCreator.class, "createCustomer("
                    + "${exchangeProperty.username},"
                    + "${exchangeProperty.firstName},"
                    + "${exchangeProperty.lastName},"
                    + "${exchangeProperty.group},"
                    + "${exchangeProperty.email})")
            .log("Send to vend queue: ${body}")
            .to("jms:queue:vend");
        
        from("jms:queue:vend")
            .log("Received Customer pre-marshal: ${body}")
            // remove headers so they don't get sent to Vend
            .removeHeaders("*")
            // remove message body since you can't send a body in a GET or DELETE
            .setBody(constant(null))
            // add authentication token to authorization header
            .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))
            .marshal().json(JsonLibrary.Gson)
            .log("Send to vend-respond post-marshall: ${body}")
            .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
            // set HTTP method
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            // send it
            .to("https://info303otago.vendhq.com/api/2.0/customers")
            // store the response
            .to("jms:queue:vend-response");     
        
    }
}
