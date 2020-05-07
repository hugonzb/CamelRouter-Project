package creator;

import domain.Account;
import domain.Customer;

public class CustomerCreator {
    
    public Customer createCustomer(Account account){
        // Creates a new customer clas using attributes from the Account class received.
        Customer customer = new Customer();
        customer.setCustomerCode(account.getUsername());
        customer.setFirstName(account.getFirstName());
        customer.setLastName(account.getLastName());
        customer.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        customer.setEmail(account.getEmail());
        
        // Returns a new Customer object.
        return customer;
    }
}
