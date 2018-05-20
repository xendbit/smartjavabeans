/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import nxt.smartcontract.api.Account;
import nxt.smartcontract.api.utils.SmartContract;
import nxt.smartcontract.api.utils.SmartMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aardvocate
 */
public class ExecuteSmartContractTest {
    
    public ExecuteSmartContractTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    //@Test
    public void testExecuteSetSimpleType() throws IOException {
        System.out.println("testExecuteSetSimpleType");
        Account owner = new Account(1565770067262084023L);       
        long id = 3623824126353402974L;

        //Testing Get Simple Type --- Might fail if the smart contract have less than 1 confirmation
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        SmartContract smartContract = new SmartContract(owner, id, fullyQualifiedClassName);
        SmartMethod smartMethod = new SmartMethod("getGreeting");
        smartContract.setSmartMethod(smartMethod);

        Account executingAccount = new Account("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        //Testing Set Simple Type --- Might fail if the smart contract have less than 1 confirmation
        smartMethod = new SmartMethod("setGreeting");
        smartMethod.setParameterValues("Hello New SJB World");
        smartMethod.setParameterTypes(String.class);
        smartContract.setSmartMethod(smartMethod);

        String result = ExecuteSmartContract.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
    }
    
    @Test
    public void testExecuteGetSimpleType() throws IOException {
        System.out.println("testExecuteSetSimpleType");
        Account owner = new Account(1565770067262084023L);       
        long id = 3623824126353402974L;

        //Testing Get Simple Type --- Might fail if the smart contract have less than 1 confirmation
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        SmartContract smartContract = new SmartContract(owner, id, fullyQualifiedClassName);
        SmartMethod smartMethod = new SmartMethod("getGreeting");
        smartContract.setSmartMethod(smartMethod);

        Account executingAccount = new Account("copper explain fated truck neat unite branch educated tenuous hum decisive notice");

        String expResult = "Hello Smart Java Beans World";
        expResult = "Hello New SJB World";
        String result = ExecuteSmartContract.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        System.out.println(resultMap);
        assertTrue(resultMap.containsKey("smartContractResult"));                
        assertEquals(expResult, resultMap.get("smartContractResult"));        
    }

    @Test
    public void testExecuteGetComplexType() throws IOException, ClassNotFoundException {
        System.out.println("testExecuteGetComplexType");
        Account owner = new Account(1565770067262084023L);
        long id = 3623824126353402974L;
        Account executingAccount = new Account("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        SmartContract smartContract = new SmartContract(owner, id, fullyQualifiedClassName);
        
        //Testing Get Complex Type --- Might fail if the set above have less than 1 confirmation
        SmartMethod smartMethod = new SmartMethod("getAccount");
        smartContract.setSmartMethod(smartMethod);

        //result = "";
        String result = ExecuteSmartContract.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        System.err.println(result);
        System.err.println(resultMap);
        assertTrue(resultMap.containsKey("smartContractResult"));
        Class returnType = Class.forName("nxt.smartcontract.api.Account");
        //This will fail if return type is wrong
        Object returnedObject = new ObjectMapper().readValue(resultMap.get("smartContractResult").toString(), returnType);
        Account c = (Account) returnedObject;
        assertEquals(c.getPassphrase(), "ibaje eniyan koda ise oluwa duro");        
    }
    
    //@Test
    public void testExecuteSetComplexType() throws IOException, ClassNotFoundException {
        System.out.println("testExecuteSetComplexType");
        Account owner = new Account(1565770067262084023L);
        long id = 3623824126353402974L;
        Account executingAccount = new Account("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        SmartContract smartContract = new SmartContract(owner, id, fullyQualifiedClassName);
        
        //Testing Set Complex Type --- Might fail if the smart contract have less than 1 confirmation
        SmartMethod smartMethod = new SmartMethod("setAccount");
        Account account = new Account("ibaje eniyan koda ise oluwa duro");
        smartMethod.setParameterValues(account);
        smartMethod.setParameterTypes(Account.class);
        smartContract.setSmartMethod(smartMethod);

        String result = ExecuteSmartContract.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
        //String parameters = "Hello, New Gree"
    }

}
