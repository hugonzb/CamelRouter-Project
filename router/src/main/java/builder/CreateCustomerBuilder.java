package builder;

import creator.AccountCreator;
import domain.Customer;
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
           .log("${body}")
           .unmarshal().json(JsonLibrary.Gson, Customer.class)
           .log("${body}")
           .to("jms:queue:create-account");
        from("jms:queue:create-account")
            .bean(AccountCreator.class, "createAccount(${exchangeProperty.id},${exchangeProperty.name})")
            .to("jms:queue:vend");
    }
}
