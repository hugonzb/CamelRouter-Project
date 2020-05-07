package creator;

import domain.Customer;

public class CustomerCreator {
    
    public Customer createCustomer(String username, String firstName, String lastName, String group, String email){
        // Creates a new customer clas using attributes from the Account class received.
        Customer customer = new Customer();
        customer.setCustomerCode(username);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setGroup("0afa8de1-147c-11e8-edec-2b197906d816");
        customer.setEmail(email);
        
        // Returns a new Customer object.
        return customer;
    }
}
