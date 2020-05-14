package creator;

import domain.Customer;

public class UpdateCustomerCreator {
    
    public Customer updateGroup(String id, String firstName, String lastName, String email){
        // Creates a new Customer class instance using attributes from the Account class received.
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setGroup("0afa8de1-147c-11e8-edec-201e0f00872c");
        customer.setEmail(email);
        
        // Returns a new Customer object.
        return customer;
    }
}
