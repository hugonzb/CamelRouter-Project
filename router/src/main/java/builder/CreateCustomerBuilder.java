package builder;

import creator.AccountCreator;
import domain.Customer;
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
           .log("Customer created: ${body}")
           .unmarshal().json(JsonLibrary.Gson, Customer.class)
           .log("Unmarshalled account: ${body}")
           .to("jms:queue:create-account");
        
        from("jms:queue:create-account")
            .bean(AccountCreator.class, "createAccount("
                    + "${exchangeProperty.id},"
                    + "${exchangeProperty.email},"
                    + "${exchangeProperty.group})")
            .to("jms:queue:vend");
        
        from("jms:queue:vend")
            // remove headers so they don't get sent to Vend
            .removeHeaders("*")
            // remove message body since you can't send a body in a GET or DELETE
            .setBody(constant(null))
            // add authentication token to authorization header
            .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))
            .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
            // set HTTP method
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            // send it
            .to("https://info303otago.vendhq.com/api/2.0/customers")
            // store the response
            .to("jms:queue:vend-response");        
    }
}
