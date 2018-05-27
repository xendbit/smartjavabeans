/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import nxt.smartcontract.api.SmartAccount;
import nxt.smartcontract.api.blockchain.request.SmartCall;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aardvocate
 */
public class CreateSmartContractTest {

    public CreateSmartContractTest() {
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

    @Test
    public void testPushSmartContract() throws Exception {
        File f = new File("logs");
        f.mkdirs();
        String passphrase = "Baba fi owo kan idodo omo oni dodo ni dodo ilu wa";
        SmartAccount account = new SmartAccount(passphrase);
        File source = new File("/Users/aardvocate/src/HelloSmartBean/src/main/java/ng/com/idempotent/hellosmartbean", "HelloSJBWorld.java");
        String className = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        String s = SmartCall.createSmartContract(account, source, className);
        Assert.assertNotNull(s);
        ObjectMapper om = new ObjectMapper();
        HashMap responseMap = om.readValue(s, HashMap.class);
        Assert.assertTrue(responseMap.containsKey("smartContractId"));
        System.err.println(responseMap.get("smartContractId"));
    }
}
