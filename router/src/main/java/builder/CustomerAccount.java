package builder;

import domain.Customer;

public class CustomerAccount {
    public Customer createAccount(String id, String email){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setEmail(email);
        customer.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        return customer;
    }
}
