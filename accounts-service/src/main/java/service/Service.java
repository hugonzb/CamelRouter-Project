package service;

import dao.AccountsDAO;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.jooby.Jooby;
import org.jooby.Results;
import org.jooby.apitool.ApiTool;
import org.jooby.json.Gzon;
import resource.AccountResource;
import resource.AccountCollectionResource;

public class Service extends Jooby {
	
	public Service() {
		
		AccountsDAO dao = new AccountsDAO();
		
		port(8086);
		
		use(new Gzon());
		
		use(new AccountCollectionResource(dao));
		use(new AccountResource(dao));
		
		use(new ApiTool().swagger(new ApiTool.Options("/swagger").use("accounts.yaml")));
		get("/", () -> Results.redirect("/swagger"));
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
