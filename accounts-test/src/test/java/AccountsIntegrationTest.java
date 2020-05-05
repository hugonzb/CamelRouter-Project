
import api.AccountApi;
import api.AccountsApi;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import domain.Account;
import java.io.IOException;
import java.util.List;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AccountsIntegrationTest {

	Retrofit retrofit = new Retrofit.Builder()
			  .baseUrl("http://localhost:8086/api/")
			  .addConverterFactory(GsonConverterFactory.create())
			  .build();

	AccountsApi accountsApi = retrofit.create(AccountsApi.class);
	AccountApi accountApi = retrofit.create(AccountApi.class);

	Account account1;
	Account account2;
	Account account3;

	@Before
	public void setUp() throws IOException {
		account1 = new Account()
				  .id("cust1")
				  .username("cust1_username")
				  .firstName("cust1_firstName")
				  .lastName("cust1_lastName")
				  .email("cust1@example.net")
				  .group("regular")
				  .uri("/api/accounts/account/cust1");

		account2 = new Account()
				  .id("cust2")
				  .username("cust2_username")
				  .firstName("cust2_firstName")
				  .lastName("cust2_lastName")
				  .email("cust2@example.net")
				  .group("regular")
				  .uri("/api/accounts/account/cust2");

		account3 = new Account()
				  .id("cust3")
				  .username("cust3_username")
				  .firstName("cust3_firstName")
				  .lastName("cust3_lastName")
				  .email("cust3@example.net")
				  .group("regular")
				  .uri("/api/accounts/account/cust3");

		accountsApi.createAccount(account1).execute();
		accountsApi.createAccount(account2).execute();
		// intentionally not creating account3
	}

	@After
	public void cleanUp() throws IOException {
		accountApi.deleteAccount(account1.getId()).execute();
		accountApi.deleteAccount(account2.getId()).execute();
		accountApi.deleteAccount(account3.getId()).execute();
	}

	@Test
	public void testCreateAccount() throws IOException {
		Response<Account> response = accountsApi.createAccount(account3).execute();
		assertThat("create account succeeded", response.isSuccessful(), is(true));
		
		List<Account> accounts = accountsApi.getAccounts().execute().body();
		assertThat("account exists at service", accounts, hasItem(account3));
		
		// test that accounts with existing IDs can not be created
		response = accountsApi.createAccount(account3).execute();
		assertThat("create duplicate account should fail", response.isSuccessful(), is(not(true)));		
		assertThat("error code should be 422", response.code(), is(422));
	}

	@Test
	public void testGetAllAccounts() throws IOException {
		List<Account> accounts = accountsApi.getAccounts().execute().body();
		assertThat(accounts, hasItems(account1, account2));
	}

	@Test
	public void testDeleteAccount() throws IOException {
		List<Account> accounts = accountsApi.getAccounts().execute().body();
		assertThat("make sure the account we are about to delete actually exists", accounts, hasItem(account1));

		Response<Void> response = accountApi.deleteAccount(account1.getId()).execute();
		assertThat("delete was successful", response.isSuccessful(), is(true));

		accounts = accountsApi.getAccounts().execute().body();
		assertThat("account should no longer exist", accounts, not(hasItem(account1)));
		
		// test that bad ID results in 404
		Response<Void> notFoundResponse = accountApi.deleteAccount("BAD ID").execute();
		assertThat("bad ID should fail", notFoundResponse.isSuccessful(), is(not(true)));
		assertThat("bad ID should cause 404", notFoundResponse.code(), is(404));
	}

	@Test
	public void testUpdateAccount() throws IOException {
		account1.setFirstName("new firstname");
		account1.setGroup("VIP");

		Response<Account> response = accountApi.updateCustomer(account1, account1.getId()).execute();
		assertThat("update should succeed",  response.isSuccessful(), is(true));

		List<Account> accounts = accountsApi.getAccounts().execute().body();

		@SuppressWarnings("null")
		Account updatedAccount = accounts.get(accounts.indexOf(account1));

		assertThat("first name updated", updatedAccount.getFirstName(), is("new firstname"));
		assertThat("group updated", updatedAccount.getGroup(), is("VIP"));
		
		// test that changing account ID is not allowed
		String originalID = account1.getId();
		account1.setId("BAD ID");
		Response<Account> conflictResponse = accountApi.updateCustomer(account1, originalID).execute();
		assertThat("changing ID should fail", conflictResponse.isSuccessful(), is(not(true)));
		assertThat("changing ID should cause 409", conflictResponse.code(), is(409));
		account1.setId(originalID);
		
		// test that bad ID results in 404
		Response<Account> notFoundResponse = accountApi.updateCustomer(account1, "BAD ID").execute();
		assertThat("bad ID should fail", notFoundResponse.isSuccessful(), is(not(true)));
		assertThat("bad ID should cause 404", notFoundResponse.code(), is(404));
		
	}

}
