package builder;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import org.apache.camel.builder.RouteBuilder;

public class CreateSaleBuilder extends RouteBuilder{
    @Override
    public void configure() throws Exception {
        from("imaps://outlook.office365.com?username=<username>@student.otago.ac.nz"
        + "&password=" + getPassword("Enter your E-Mail password")
        + "&searchTerm.subject=Vend:SaleUpdate"
        + "&debugMode=false"  // set to true if you want to see the authentication details
        + "&folderName=INBOX")  // change to whatever folder your Vend messages end up in
        .convertBodyTo(String.class)
        .log("${body}")
        .to("jms:queue:vend-new-sale");
        

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
