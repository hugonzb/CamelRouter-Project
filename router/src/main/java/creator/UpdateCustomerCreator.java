package creator;

import domain.Account;
import domain.Customer;

public class UpdateCustomerCreator {
    
    public Customer updateGroup(Account account){
        // Creates a new Customer class instance using attributes from the Account class received.
        Customer customer = new Customer();
        customer.setCustomerCode(account.getUsername());
        customer.setFirstName(account.getFirstName());
        customer.setLastName(account.getLastName());
        customer.setGroup("0afa8de1-147c-11e8-edec-201e0f00872c");
        customer.setEmail(account.getEmail());
        
        // Returns a new Customer object.
        return customer;
    }
}
