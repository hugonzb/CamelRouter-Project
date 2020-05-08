package builder;

import creator.UpdateCustomerCreator;
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
        from("imaps://outlook.office365.com?username=baihu868@student.otago.ac.nz"
            + "&password=" + getPassword("Enter your E-Mail password")
            + "&searchTerm.subject=Vend:SaleUpdate"
            + "&debugMode=false"  // set to true if you want to see the authentication details
            + "&folderName=INBOX")  // change to whatever folder your Vend messages end up in
            .convertBodyTo(String.class)
            .log("Sale received from Vend: ${body}")
            .to("jms:queue:vend-new-sale");
        
        from("jms:queue:vend-new-sale")
            .unmarshal().json(JsonLibrary.Gson, Sale.class)
            .log("Formatted sale: ${body}")
            .setProperty("Customer_Id").simple("${body.customer.id}")
            .setProperty("Customer_Group").simple("${body.customer.group}")
            .setProperty("Customer_Email").simple("${body.customer.email}")
            .setProperty("Customer_FirstName").simple("${body.customer.firstName}")
            .setProperty("Customer_LastName").simple("${body.customer.lastName}")
            .to("jms:queue:sale-data");
        
        from("jms:queue:sale-data")
            .marshal().json(JsonLibrary.Gson)
            .removeHeaders("*")
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .to("http://localhost:8081/api/sales")
            .to("jms:queue:get-sale-summary");
        
        from("jms:queue:get-sale-summary")
            .removeHeaders("*")
            .setBody(constant(null))
            .setHeader(Exchange.HTTP_METHOD, constant("GET"))
            .recipientList()
                .simple("http://localhost:8081/api/sales/customer/${exchangeProperty.Customer_Id}/summary")
            .log("Customer Summary: ${body}")
            .to("jms:queue:sale-summary-response");
        
        from("jms:queue:sale-summary-response")
            .unmarshal().json(JsonLibrary.Gson, Summary.class)
            .log("Unmarshaled Customer Summary: ${body}")
                .choice()
                    .when().simple("${body.group} == ${exchangeProperty.Customer_Group}")
                        .log("Group does not need updating: ${body.group} does not equal ${exchangeProperty.Customer_Group}")
                        .to("jms:queue:update-not-required")
                .otherwise()
                    .log("Group must be updated: ${body.group} does not equal ${exchangeProperty.Customer_Group}")
                    .to("jms:queue:update-customer-group");    
        from("jms:queue:update-customer-group")
                .log("Customer before updating: ${body}")
                .bean(UpdateCustomerCreator.class, "updateGroup(${body})")
                .log("Customer after updating: ${body}")
                .to("jms:queue:updated-customer-group");
    }
    
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
