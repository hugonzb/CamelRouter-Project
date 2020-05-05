package service;

import dao.SaleDAO;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.jooby.Jooby;
import org.jooby.Results;
import org.jooby.apitool.ApiTool;
import org.jooby.json.Gzon;
import resource.CustomerSalesResource;
import resource.SalesResource;

public class Service extends Jooby {

	public Service() {
		SaleDAO dao = new SaleDAO();

		port(8081);

		use(new Gzon());

		use(new SalesResource(dao));
		use(new CustomerSalesResource(dao));

		use(new ApiTool().swagger(new ApiTool.Options("/swagger").use("sales.yaml")));
		get(() -> Results.redirect("/swagger"));
	}

	public static void main(String[] args) throws IOException {
		Service server = new Service();

		CompletableFuture.runAsync(() -> {
			server.start();
		});

		server.onStarted(() -> {
			System.out.println("\nPress Enter to stop service.");
		});

		System.in.read();
		System.exit(0);
	}

}
