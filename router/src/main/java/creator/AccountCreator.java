package creator;

import domain.Customer;

public class AccountCreator {
    public Customer createAccount(String firstName, String lastName, String customerCode, String email){
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setCustomerCode("0afa8de1-147c-11e8-edec-2b197906d816");
        customer.setEmail(email);
        return customer;
    }
}
