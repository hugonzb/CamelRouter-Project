package builder;

import creator.AccountCreator;
import creator.UpdateCustomerCreator;
import creator.changeGroupToId;
import domain.Sale;
import domain.Summary;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

public class CreateSaleBuilder extends RouteBuilder{
    
    @Override
    public void configure() throws Exception {
        
        // Read emails with "Vend:SaleUpdate" subject.
        from("imaps://outlook.office365.com?username=baihu868@student.otago.ac.nz"
            + "&password=" + getPassword("Enter your E-Mail password")
            + "&searchTerm.subject=Vend:SaleUpdate"
            + "&debugMode=false" 
            + "&folderName=INBOX") 
            .convertBodyTo(String.class)
            .log("Sale received from Vend: ${body}")
            .to("jms:queue:vend-new-sale");
        
        // Create Sale object and store information in exchange properties.
        from("jms:queue:vend-new-sale")
            .unmarshal().json(JsonLibrary.Gson, Sale.class)
            .log("Formatted sale: ${body}")
            .setProperty("Customer_Id").simple("${body.customer.id}")
            .setProperty("Customer_Group").simple("${body.customer.group}")
            .setProperty("Customer_Email").simple("${body.customer.email}")
            .setProperty("Customer_FirstName").simple("${body.customer.firstName}")
            .setProperty("Customer_LastName").simple("${body.customer.lastName}")
            .to("jms:queue:sale-data");
        
        // POST to sales service.
        from("jms:queue:sale-data")
            .removeHeaders("*")
            .marshal().json(JsonLibrary.Gson)
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .to("http://localhost:8081/api/sales")
            .to("jms:queue:get-sale-summary");
        
        // GET sale summary of customer based on their ID.
        from("jms:queue:get-sale-summary")
            .removeHeaders("*")
            .setBody(constant(null))
            .setHeader(Exchange.HTTP_METHOD, constant("GET"))
            .recipientList()
                .simple("http://localhost:8081/api/sales/customer/${exchangeProperty.Customer_Id}/summary")
            .log("Customer Summary: ${body}")
            .to("jms:queue:summary-response");
        
        // Check to see if customer group needs to be updated.
        from("jms:queue:summary-response")
            .unmarshal().json(JsonLibrary.Gson, Summary.class)
            .log("Unmarshaled Customer Summary: ${body}")
            .bean(changeGroupToId.class, "changeGroup(${body})")
            .log("Group field changed to vend ID group: ${body}" )
                .choice()
                    .when().simple("${body.group} == ${exchangeProperty.Customer_Group}")
                        .log("Group does not need updating: ${body.group} does not equal ${exchangeProperty.Customer_Group}")
                        .to("jms:queue:update-not-required")
                .otherwise()
                    .log("Group must be updated: ${body.group} does not equal ${exchangeProperty.Customer_Group}")
                    .to("jms:queue:update-customer-group");    
        
        // Update customer group to "VIP Customers".
        from("jms:queue:update-customer-group")
                .log("Customer before updating: ${body}")
                .bean(UpdateCustomerCreator.class, "updateGroup(${exchangeProperty.Customer_Id},"
                        + "${exchangeProperty.Customer_FirstName}, "
                        + "${exchangeProperty.Customer_LastName}, "
                        + "${exchangeProperty.Customer_Email})")
                .log("Customer after updating: ${body}")
                .multicast()
                .to("jms:queue:put-vend", "jms:queue:put-account-service");
        
        // Sends PUT request to Vend to change group of existing customer.
        from("jms:queue:put-vend")
            .log("${body.id}")
            .removeHeaders("*")
            .setHeader("Authorization", constant("Bearer KiQSsELLtocyS2WDN5w5s_jYaBpXa0h2ex1mep1a"))
            .marshal().json(JsonLibrary.Gson) 
            .setHeader(Exchange.CONTENT_TYPE).constant("application/json")
            .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
            .log("Send to vend to update: ${body}")
            .recipientList()
                .simple("https://info303otago.vendhq.com/api/2.0/customers/${exchangeProperty.Customer_Id}")
            .to("jms:queue:vend-updated-customer-response");  
        
        // Sends PUT request to Account service at port 8086.
        from("jms:queue:put-account-service")
            .bean(AccountCreator.class, "createAccount(${body})")
            .log("Updated Account: ${body}")
            .marshal().json(JsonLibrary.Gson)
            .removeHeaders("*")
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
            .recipientList()
                .simple("http://localhost:8086/api/accounts/account/${exchangeProperty.Customer_Id}")
            .to("jms:queue:vend-updated-account-service-response");          
    }
    
    // Displays password prompt box for email.
    public static String getPassword(String prompt) {
        JPasswordField txtPasswd = new JPasswordField();
        int resp = JOptionPane.showConfirmDialog(null, txtPasswd, prompt,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (resp == JOptionPane.OK_OPTION) {
            String password = new String(txtPasswd.getPassword());
            return password;
        }
        return null;
    }
}
