package mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.io.IOException;

/**
 * @author Mark George <mark.george@otago.ac.nz>
 */
public class MockMailServer {

	public static void main(String[] args) throws IOException {
		String subject = "Vend:SaleUpdate";

		String body = "{\"created_at\":\"2019-08-31 17:03:09\",\"customer\":{\"balance\":\"0.00000\",\"company_name\":\"\",\"contact_first_name\":\"Doris\",\"contact_last_name\":\"Dolores\",\"created_at\":\"2018-03-12 06:43:05\",\"custom_field_1\":\"\",\"custom_field_2\":\"\",\"custom_field_3\":\"\",\"custom_field_4\":\"\",\"customer_code\":\"Doris-9CR9\",\"customer_group_id\":\"0afa8de1-147c-11e8-edec-2b197906d816\",\"date_of_birth\":null,\"deleted_at\":null,\"do_not_email\":false,\"email\":\"doris@example.net\",\"enable_loyalty\":false,\"fax\":\"\",\"first_name\":\"Doris\",\"id\":\"0afa8de1-147c-11e8-edec-25c09e4a6f05\",\"last_name\":\"Dolores\",\"loyalty_balance\":\"0.00000\",\"mobile\":\"\",\"note\":\"\",\"phone\":\"\",\"points\":0,\"sex\":\"F\",\"updated_at\":\"2019-08-31 17:03:10\",\"year_to_date\":\"3127.19995\"},\"customer_id\":\"0afa8de1-147c-11e8-edec-25c09e4a6f05\",\"deleted_at\":null,\"id\":\"fa7d04ba-c21e-b574-11e9-cc112b0cff05\",\"invoice_number\":\"19\",\"note\":\"\",\"outlet_id\":\"0afa8de1-1450-11e8-edec-056e6ece24a4\",\"register_id\":\"0afa8de1-1450-11e8-edec-056e6ecf4cd9\",\"register_sale_payments\":[{\"amount\":339.8,\"id\":\"fa7d04ba-c21e-b574-11e9-cc11348cf12f\",\"payment_date\":\"2019-08-31T17:03:07Z\",\"payment_type\":{\"has_native_support\":false,\"id\":\"3\",\"name\":\"Credit Card\"},\"payment_type_id\":3,\"retailer_payment_type\":{\"config\":null,\"id\":\"0afa8de1-1450-11e8-edec-056e6ed079b9\",\"name\":\"Credit Card\",\"payment_type_id\":\"3\"},\"retailer_payment_type_id\":\"0afa8de1-1450-11e8-edec-056e6ed079b9\"}],\"register_sale_products\":[{\"discount\":\"0.00000\",\"id\":\"fa7d04ba-c21e-b574-11e9-cc112e9d0397\",\"is_return\":false,\"loyalty_value\":\"0.00000\",\"note\":null,\"price\":\"234.69565\",\"price_set\":false,\"price_total\":\"234.69565\",\"product_id\":\"0afa8de1-147c-11e8-edec-056e6f0be097\",\"quantity\":1,\"tax\":\"35.20435\",\"tax_id\":\"0afa8de1-1450-11e8-edec-056e6ec70277\",\"tax_total\":\"35.20435\"},{\"discount\":\"0.00000\",\"id\":\"fa7d04ba-c21e-b574-11e9-cc112fe4299a\",\"is_return\":false,\"loyalty_value\":\"0.00000\",\"note\":null,\"price\":\"60.78261\",\"price_set\":false,\"price_total\":\"60.78261\",\"product_id\":\"0afa8de1-147c-11e8-edec-056e701f4190\",\"quantity\":1,\"tax\":\"9.11739\",\"tax_id\":\"0afa8de1-1450-11e8-edec-056e6ec70277\",\"tax_total\":\"9.11739\"}],\"return_for\":null,\"sale_date\":\"2019-08-31T17:03:08Z\",\"short_code\":\"avywsc\",\"source\":\"USER\",\"source_id\":null,\"status\":\"CLOSED\",\"taxes\":[{\"id\":\"6ecd4ad7-056e-11e8-adec-0afa8de11450\",\"name\":\"GST\",\"rate\":\"0.15000\",\"tax\":44.32174}],\"totals\":{\"total_loyalty\":\"0.00000\",\"total_payment\":\"339.80000\",\"total_price\":\"295.47826\",\"total_tax\":\"44.32174\",\"total_to_pay\":\"0.00000\"},\"updated_at\":\"2019-08-31T17:03:09+00:00\",\"user\":{\"created_at\":\"2018-02-12 23:49:42\",\"display_name\":\"Mark George\",\"email\":\"mark.george@otago.ac.nz\",\"id\":\"0afa8de1-147c-11e8-edec-104f6535a398\",\"name\":\"mgeorge\",\"target_daily\":null,\"target_monthly\":null,\"target_weekly\":null,\"updated_at\":\"2018-02-13 03:50:57\"},\"user_id\":\"0afa8de1-147c-11e8-edec-104f6535a398\",\"version\":11820759142}\n";

		// create a mock server than can receive (SMTP) and host (IMAP)
		GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP_IMAP);

		// add a user
		greenMail.setUser("test@localhost", "test@localhost", "password");

		// start the server
		greenMail.start();

		System.out.println("IMAP Server Ready on port " + greenMail.getImap().getPort());
		System.out.println("Press Enter to send an E-Mail ...");

		// a loop for sending messages
		while (true) {

			// let the user press enter to send a message
			System.out.print("Press Enter to send an E-Mail ...");
			System.in.read();

			// send a message
			GreenMailUtil.sendTextEmailTest("test@localhost", "test@localhost", subject, body);
			System.out.println(" sent.");
		}

	}
}
