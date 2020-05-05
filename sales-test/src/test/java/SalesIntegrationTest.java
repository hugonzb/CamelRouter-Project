
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import domain.Customer;
import domain.Sale;
import domain.SaleItem;
import api.SalesApi;
import api.SalesForCustomerApi;
import domain.Summary;
import domain.Totals;
import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import java.math.BigDecimal;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import retrofit2.Response;

public class SalesIntegrationTest {

	Retrofit retrofit = new Retrofit.Builder()
			  .baseUrl("http://localhost:8081/api/")
			  .addConverterFactory(GsonConverterFactory.create())
			  .build();

	SalesApi sales = retrofit.create(SalesApi.class);
	SalesForCustomerApi customers = retrofit.create(SalesForCustomerApi.class);

	Customer cust1;
	Customer cust2;
	Sale sale1;
	Sale sale2;
	Sale sale3;
	Sale sale4;

	@Before
	public void setUp() throws IOException {
		cust1 = new Customer()
				  .id("cust1_id")
				  .email("cust1@example.net")
				  .customerGroupId("regular");

		cust2 = new Customer()
				  .id("cust2_id")
				  .email("cust2@example.net")
				  .customerGroupId("regular");

		sale1 = new Sale()
				  .id("1")
				  .saleDate("today")
				  .customer(cust1)
				  .addRegisterSaleProductsItem(new SaleItem().price(100.00).productId("12345").quantity(2.0))
				  .totals(new Totals().totalPayment(200.00).totalPrice(200.00).totalTax(0.0));

		sale2 = new Sale()
				  .id("2")
				  .saleDate("today")
				  .customer(cust1)
				  .addRegisterSaleProductsItem(new SaleItem().price(5000.00).productId("54321").quantity(2.0))
				  .totals(new Totals().totalPayment(10000.00).totalPrice(10000.00).totalTax(0.0));

		sale3 = new Sale()
				  .id("3")
				  .saleDate("today")
				  .customer(cust2)
				  .addRegisterSaleProductsItem(new SaleItem().price(100.00).productId("12345").quantity(2.0))
				  .totals(new Totals().totalPayment(100.00).totalPrice(100.00).totalTax(0.0));

		sale4 = new Sale()
				  .id("4")
				  .saleDate("today")
				  .customer(cust1)
				  .addRegisterSaleProductsItem(new SaleItem().price(100.00).productId("12345").quantity(2.0))
				  .totals(new Totals().totalPayment(100.00).totalPrice(100.00).totalTax(0.0));

		sales.addNewSale(sale1).execute();
		sales.addNewSale(sale2).execute();
		sales.addNewSale(sale3).execute();
		// intentionally not adding sale4
	}

	@After
	public void cleanUp() throws IOException {
		sales.deleteSale(sale1.getId()).execute();
		sales.deleteSale(sale2.getId()).execute();
		sales.deleteSale(sale3.getId()).execute();
		sales.deleteSale(sale4.getId()).execute();
	}
	
	@Test
	public void testCreateSale() throws IOException {
		Response<Sale> response = sales.addNewSale(sale4).execute();
		assertThat("create sale succeeded", response.isSuccessful(), is(true));
		
		List<Sale> salesResponse = customers.getCustomerSales(cust1.getId()).execute().body();
		assertThat("new sale should exists on service", salesResponse, hasItem(sale4));
		
		Response<Sale> duplicateResponse = sales.addNewSale(sale4).execute();
		assertThat("creating a duplicate sale should fail", duplicateResponse.isSuccessful(), is(not(true)));
		assertThat("creating a duplicate sale should cause a 422", duplicateResponse.code(), is(422));
	}

	@Test
	public void testDeleteSale() throws IOException {
		List<Sale> salesResponse = customers.getCustomerSales(cust1.getId()).execute().body();
		assertThat("sale we are about to delete should currently exist", salesResponse, hasItem(sale1));
		
		Response<Void> response = sales.deleteSale(sale1.getId()).execute();
		assertThat("delete sale succeeded", response.isSuccessful(), is(true));		

		salesResponse = customers.getCustomerSales(cust1.getId()).execute().body();
		assertThat("delete sale should no longer exist", salesResponse, not(hasItem(sale1)));	

		Response<Void> notFoundResponse = sales.deleteSale("BAD ID").execute();
		assertThat("deleting with a bad ID should fail", notFoundResponse.isSuccessful(), is(not(true)));		
		assertThat("deleting with a bad ID should cause a 404", notFoundResponse.code(), is(404));		
	}	
	
	@Test
	public void testGetSales() throws IOException {
		List<Sale> salesResponse = customers.getCustomerSales(cust1.getId()).execute().body();
		assertThat(salesResponse, hasItems(sale1, sale2));
		assertThat(salesResponse, not(hasItem(sale3)));
	}

	@Test
	@SuppressWarnings("null")
	public void testGetSummary() throws IOException {
		Summary summary = customers.getCustomerSummary(cust1.getId()).execute().body();
		assertThat(summary.getNumberOfSales(), is(2));
		assertThat(summary.getTotalPayment(), is(new BigDecimal("10200.0")));
		assertThat(summary.getGroup(), is("VIP Customers"));
	}

}
