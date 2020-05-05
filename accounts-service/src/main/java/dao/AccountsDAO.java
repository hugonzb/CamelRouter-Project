package dao;

import domain.Account;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AccountsDAO {

	private static final Map<String, Account> customers = new HashMap<>();

	public Collection<Account> getAll() {
		return new ArrayList<>(customers.values());
	}

	public void save(Account customer) {
		customers.put(customer.getId(), customer);
	}

	public Account get(String id) {
		return customers.get(id);
	}

	public void delete(String id) {
		customers.remove(id);
	}

	public void update(String id, Account updatedCustomer) {
		customers.put(id, updatedCustomer);
	}

	public boolean exists(String id) {
		return customers.containsKey(id);
	}

}
