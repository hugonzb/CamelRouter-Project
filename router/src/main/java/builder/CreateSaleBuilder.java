package builder;

import domain.Sale;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
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
