/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;


import nxt.smartcontract.api.Account;
import nxt.smartcontract.api.SmartContract;
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

    /**
     * Test of executeSmartContract method, of class ExecuteSmartContract.
     */
    @Test
    public void testExecuteSmartContract() {
        System.out.println("executeSmartContract");
        Account owner = new Account(1565770067262084023L);
        long id = 891585751711412746L;
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        String methodName = "getGreeting";
        String parameters = null;
        String parameterTypes = null;
        
        SmartContract contract = new SmartContract(owner, id, fullyQualifiedClassName, methodName, parameters, parameterTypes);
        Account executingAccount = new Account("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        String expResult = "Hello Smart Java Beans World";
        String result = ExecuteSmartContract.executeSmartContract(contract, executingAccount);
        assertEquals(expResult, result);
    }
    
}
