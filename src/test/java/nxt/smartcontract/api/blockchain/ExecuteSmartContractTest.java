/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;

import nxt.smartcontract.api.SmartMethod;
import nxt.smartcontract.api.SmartClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import nxt.smartcontract.api.SmartAccount;
import nxt.smartcontract.api.blockchain.request.SmartCall;
import nxt.smartcontract.api.utils.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

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
        long id = 1666950666991748874L;

        //Testing Get Simple Type --- Might fail if the smart contract have less than 1 confirmation
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        SmartClass smartContract = new SmartClass(id, fullyQualifiedClassName);
        SmartMethod smartMethod = new SmartMethod("getGreeting");
        smartContract.setSmartMethod(smartMethod);

        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        //Testing Set Simple Type --- Might fail if the smart contract have less than 1 confirmation
        smartMethod = new SmartMethod("setGreeting");
        smartMethod.setParameterValues("Hello New SJB World");
        smartMethod.setParameterTypes(String.class);
        smartContract.setSmartMethod(smartMethod);

        String result = nxt.smartcontract.api.blockchain.request.SmartCall.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
    }
    
    //@Test
    public void testExecuteGetSimpleType() throws IOException {
        System.out.println("testExecuteSetSimpleType");
        long id = 1666950666991748874L;

        //Testing Get Simple Type --- Might fail if the smart contract have less than 1 confirmation
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        SmartClass smartContract = new SmartClass(id, fullyQualifiedClassName);
        SmartMethod smartMethod = new SmartMethod("getGreeting");
        smartContract.setSmartMethod(smartMethod);

        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");

        String expResult = "Hello Smart Java Beans World";
        expResult = "Hello New SJB World";
        String result = SmartCall.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        System.out.println(resultMap);
        assertTrue(resultMap.containsKey("smartContractResult"));                
        assertEquals(expResult, resultMap.get("smartContractResult"));        
    }

    //@Test
    public void testExecuteGetComplexType() throws IOException, ClassNotFoundException {
        System.out.println("testExecuteGetComplexType");
        long id = 1666950666991748874L;
        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        SmartClass smartContract = new SmartClass(id, fullyQualifiedClassName);
        
        //Testing Get Complex Type --- Might fail if the set above have less than 1 confirmation
        SmartMethod smartMethod = new SmartMethod("getAccount");
        smartContract.setSmartMethod(smartMethod);

        //result = "";
        String result = SmartCall.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        System.err.println(result);
        System.err.println(resultMap);
        assertTrue(resultMap.containsKey("smartContractResult"));
        Class returnType = Class.forName("nxt.smartcontract.api.Account");
        //This will fail if return type is wrong
        Object returnedObject = new ObjectMapper().readValue(resultMap.get("smartContractResult").toString(), returnType);
        SmartAccount c = (SmartAccount) returnedObject;
        assertEquals(c.getPassphrase(), "ibaje eniyan koda ise oluwa duro");        
    }
    
    //@Test
    public void testExecuteSetComplexType() throws IOException, ClassNotFoundException {
        System.out.println("testExecuteSetComplexType");
        long id = 1666950666991748874L;
        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        nxt.smartcontract.api.SmartClass smartContract = new SmartClass(id, fullyQualifiedClassName);
        
        //Testing Set Complex Type --- Might fail if the smart contract have less than 1 confirmation
        SmartMethod smartMethod = new SmartMethod("setAccount");
        SmartAccount account = new SmartAccount("ibaje eniyan koda ise oluwa duro");
        smartMethod.setParameterValues(account);
        smartMethod.setParameterTypes(SmartAccount.class);
        smartContract.setSmartMethod(smartMethod);

        String result = SmartCall.executeSmartContract(smartContract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
        //String parameters = "Hello, New Gree"
    }
    
    //@Test
    public void testExecuteURLGet() throws Exception {
        System.out.println("testExecuteURLGet");
        long id = 6466152329223480814L;
        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";        
        
        SmartMethod smartMethod = new SmartMethod("sendGET");
        smartMethod.setParameterTypes(String.class);
        smartMethod.setParameterValues("http://localhost:8987/nxt?requestType=getAccount&account=1565770067262084023");
        SmartClass smartClass = new SmartClass(id, fullyQualifiedClassName);
        smartClass.setSmartMethod(smartMethod);
        
        String result = SmartCall.executeSmartContract(smartClass, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
    }    
    
    @Test
    public void testExecuteSendMoney() throws Exception {
        System.out.println("testExecuteSendMoney");
        long id = 5386226739023004142L;
        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        SmartAccount sender = new SmartAccount("Baba fi owo kan idodo omo oni dodo ni dodo ilu wa");
        SmartAccount recipient = new SmartAccount("useful progressive explode restoration estimate script cottage dismiss meaning squeeze activity pastel");
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";        
        
        SmartMethod smartMethod = new SmartMethod("sendMoney");
        smartMethod.setParameterTypes(SmartAccount.class, SmartAccount.class, Long.class);
        smartMethod.setParameterValues(sender, recipient, (100 * Constants.ONE_NXT));
        SmartClass smartClass = new SmartClass(id, fullyQualifiedClassName);
        smartClass.setSmartMethod(smartMethod);
        
        String result = SmartCall.executeSmartContract(smartClass, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
    }        
    
    //@Test
    public void testExecuteSendMessage() throws Exception {
        System.out.println("testExecuteSendMessage");
        long id = 7241189096096398119L;
        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        SmartAccount sender = new SmartAccount("Baba fi owo kan idodo omo oni dodo ni dodo ilu wa");
        SmartAccount recipient = new SmartAccount("useful progressive explode restoration estimate script cottage dismiss meaning squeeze activity pastel");
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";        
        
        SmartMethod smartMethod = new SmartMethod("sendMessage");
        smartMethod.setParameterTypes(SmartAccount.class, SmartAccount.class, String.class);
        smartMethod.setParameterValues(sender, recipient, "Hello Smart Java Beans World");
        SmartClass smartClass = new SmartClass(id, fullyQualifiedClassName);
        smartClass.setSmartMethod(smartMethod);
        
        String result = SmartCall.executeSmartContract(smartClass, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
    }            
}
