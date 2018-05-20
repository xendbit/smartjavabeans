/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import nxt.smartcontract.api.Account;
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
        Account account = new Account(passphrase);
        String source = "package ng.com.idempotent.hellosmartbean;\n"
                + "\n"
                + "import nxt.smartcontract.api.Account;\n"
                + "import nxt.smartcontract.api.Contract;\n"
                + "\n"
                + "\n"
                + "public class HelloSJBWorld implements Contract {            \n"
                + "    private String greeting = \"Hello Smart Java Beans World\";\n"
                + "    private String isPrivate;\n"
                + "    private Account account;\n"
                + "\n"
                + "    public void setAccount(Account account) {\n"
                + "        this.account = account;\n"
                + "    }\n"
                + "\n"
                + "    public Account getAccount() {\n"
                + "        return account;\n"
                + "    }\n"
                + "    \n"
                + "    public String getIsPrivate() {\n"
                + "        return isPrivate;\n"
                + "    }\n"
                + "\n"
                + "    public void setIsPrivate(String isPrivate) {\n"
                + "        this.isPrivate = isPrivate;\n"
                + "    }\n"
                + "\n"
                + "    public String getHello() {\n"
                + "        return \"Hello SJB World\";\n"
                + "    }\n"
                + "    \n"
                + "    public void setGreeting(String greeting) {\n"
                + "        this.greeting = greeting;\n"
                + "    }\n"
                + "    \n"
                + "    public String getGreeting() {\n"
                + "        return greeting;\n"
                + "    }  \n"
                + "    \n"
                + "    @Override\n"
                + "    public String toString() {\n"
                + "        return super.toString();\n"
                + "    }   \n"
                + "}\n";
        String className = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        String s = CreateSmartContract.pushSmartContract(account, source, className);
        Assert.assertNotNull(s);
        ObjectMapper om = new ObjectMapper();
        HashMap responseMap = om.readValue(s, HashMap.class);
        Assert.assertTrue(responseMap.containsKey("smartContractId"));
        System.err.println(responseMap.get("smartContractId"));
    }
}
