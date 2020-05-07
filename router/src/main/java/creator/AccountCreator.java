package creator;

import domain.Account;
import domain.Customer;

public class AccountCreator {
    
    public Account createAccount(Customer customer){
        // Creates a new Account class instance using attributes from the Customer class received.
        Account account = new Account();
        account.setId(customer.getId());
        account.setUsername(customer.getCustomerCode());
        account.setFirstName(customer.getFirstName());
        account.setLastName(customer.getLastName());
        account.setGroup(customer.getGroup());
        account.setEmail(account.getEmail());
        
        // Returns a new Account object.
        return account;
    }
}
