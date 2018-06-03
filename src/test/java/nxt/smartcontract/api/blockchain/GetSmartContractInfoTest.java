/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import nxt.smartcontract.api.SmartAccount;
import nxt.smartcontract.api.blockchain.request.SmartCall;
import nxt.smartcontract.api.SmartClass;
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
public class GetSmartContractInfoTest {

    public String baseURL;
    
    public GetSmartContractInfoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        baseURL = "http://idempotent.com.ng:8987/nxt?";
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetSmartContractInfo() throws JsonProcessingException, IOException {
        System.out.println("testGetSmartContractInfo");
        long id = 8381013826859170805L;
        SmartClass contract = new SmartClass(id);
        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        String result = SmartCall.getSmartContractInfo(baseURL, contract, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        System.out.println(resultMap);
        HashMap smartContractInfoMap = new ObjectMapper().readValue(resultMap.get("smartContractInfo").toString(), HashMap.class);
        assertTrue(smartContractInfoMap.containsKey("name"));
        assertTrue(smartContractInfoMap.containsKey("methods"));
    }
}
