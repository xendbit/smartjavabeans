package ng.com.idempotent.hellosmartbean;

import nxt.smartcontract.api.Account;
import nxt.smartcontract.api.Contract;


public class HelloSJBWorld implements Contract {        
    private Account account;
    private String greeting = "Hello Smart Java Beans World";
    private String isPrivate;

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }    
    
    public String getHello() {
        return "Hello SJB World";
    }
    
    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
    
    public String getGreeting() {
        return greeting;
    }  
    
    @Override
    public String toString() {
        return super.toString();
    }
}
