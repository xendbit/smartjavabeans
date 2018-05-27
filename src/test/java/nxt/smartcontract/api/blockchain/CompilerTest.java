/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import ng.com.idempotent.CompiledCode;
import static ng.com.idempotent.Compiler.compile;
import ng.com.idempotent.CustomOIS;
import ng.com.idempotent.DynamicClassLoader;
import nxt.smartcontract.api.SmartAccount;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author aardvocate
 */
@SuppressWarnings("unchecked")
public class CompilerTest {

    public CompilerTest() {
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
     * Test of compile method, of class Compiler.
     */
    @Test
    public void testCompile() throws Exception {
        System.out.println("CompilerTest.testCompile");
        String className = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";
        File source = new File("/Users/aardvocate/src/HelloSmartBean/src/main/java/ng/com/idempotent/hellosmartbean", "HelloSJBWorld.java");
        HashMap<String, Object> compiledMap = compile(source, className);
        //System.out.println(compiledMap);
        assertTrue(compiledMap.containsKey("documentation"));
        assertTrue(compiledMap.containsKey("byteCode"));
        System.out.println(compiledMap.get("byteCode").toString());

        byte[] byteCode = Base64.getDecoder().decode(compiledMap.get("byteCode").toString());

        DynamicClassLoader classLoader = new DynamicClassLoader(this.getClass().getClassLoader());
        CompiledCode cc = new CompiledCode(className);
        cc.setByteCode(byteCode);

        classLoader.addCode(cc);

        List<HashMap<String, Object>> methodsList = (List<HashMap<String, Object>>) ((HashMap<String, Object>) compiledMap.get("documentation")).get("methods");
        String name = ((HashMap<String, Object>) compiledMap.get("documentation")).get("name").toString();
        assertEquals(name, className);

        Class classInstance = classLoader.getClass(name);
        System.out.println(classInstance.getName());

        Object instance = classInstance.newInstance();

        Method m = instance.getClass().getMethod("getHello", new Class[]{});
        String s = m.invoke(instance, new Object[]{}).toString();
        assertEquals(s, "Hello SJB World");

        m = instance.getClass().getMethod("getGreeting", new Class[]{});
        s = m.invoke(instance, new Object[]{}).toString();
        assertEquals(s, "Hello Smart Java Beans World");

        m = instance.getClass().getMethod("setGreeting", new Class[]{Class.forName("java.lang.String")});
        m.invoke(instance, new Object[]{"This is a new Greeting"});

        m = instance.getClass().getMethod("getGreeting", new Class[]{});
        s = m.invoke(instance, new Object[]{}).toString();
        assertEquals(s, "This is a new Greeting");

        //Serialize and Deserialize Object, see if the new Greeting is Serialized
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(baos);
        os.writeObject(instance);

        byte[] newObject = baos.toByteArray();

        ByteArrayInputStream bis = new ByteArrayInputStream(newObject);
        CustomOIS ois = new CustomOIS(bis, classLoader);
        instance = ois.readObject();

        m = instance.getClass().getMethod("getGreeting", new Class[]{});
        s = m.invoke(instance, new Object[]{}).toString();
        assertEquals(s, "This is a new Greeting");
    }

    //@Test
    public void testVoting() throws Exception {
        System.out.println("CompilerTest.testVoting");
        String className = "ng.com.idempotent.hellosmartbean.VotingBean";
        File source = new File("/Users/aardvocate/src/HelloSmartBean/src/main/java/ng/com/idempotent/hellosmartbean", "VotingBean.java");
        HashMap<String, Object> compiledMap = new HashMap<>();
        compiledMap = compile(source, className);
        System.out.println(compiledMap);
        assertTrue(compiledMap.containsKey("documentation"));
        assertTrue(compiledMap.containsKey("byteCode"));
        System.out.println(compiledMap.get("byteCode").toString());

        byte[] byteCode = Base64.getDecoder().decode(compiledMap.get("byteCode").toString());

        DynamicClassLoader classLoader = new DynamicClassLoader(this.getClass().getClassLoader());
        CompiledCode cc = new CompiledCode(className);
        cc.setByteCode(byteCode);

        classLoader.addCode(cc);

        Class classInstance = classLoader.getClass(className);

        Object instance = classInstance.newInstance();

        SmartAccount[] smartAccounts = {
            new SmartAccount("Voter One"),
            new SmartAccount("Voter Two"),
            new SmartAccount("Voter Three"),
            new SmartAccount("Voter Four"),
            new SmartAccount("Voter Five"),};
        //registerVoter
        Method m = classInstance.getMethod("registerVoter", new Class[]{SmartAccount.class});
        for (SmartAccount smartAccount : smartAccounts) {
            m.invoke(instance, smartAccount);
        }
        
        //getRegistered
        m = classInstance.getMethod("getRegistered", new Class[]{});
        Set<SmartAccount> registered = (Set<SmartAccount>) m.invoke(instance, new Object[]{});
        assertEquals(registered.size(), smartAccounts.length);
        
        //Vote
        m = classInstance.getMethod("vote", new Class[]{SmartAccount.class});
        m.invoke(instance, smartAccounts[0]);
        m.invoke(instance, smartAccounts[2]);
        m.invoke(instance, smartAccounts[4]);
        //check stderr for a message saying this voter tried to vote twice
        m.invoke(instance, smartAccounts[0]);
        
        //getVoters
        m = classInstance.getMethod("getVoters", new Class[]{});
        Set<SmartAccount> voters = (Set<SmartAccount>) m.invoke(instance, new Object[]{});
        assertEquals(voters.size(), 3);
        
        //Serialize and Deserialize Object, see if the new Greeting is Serialized
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(baos);
        os.writeObject(instance);

        byte[] newObject = baos.toByteArray();

        ByteArrayInputStream bis = new ByteArrayInputStream(newObject);
        CustomOIS ois = new CustomOIS(bis, classLoader);
        instance = ois.readObject();
        
        //getRegistered
        m = classInstance.getMethod("getRegistered", new Class[]{});
        registered = (Set<SmartAccount>) m.invoke(instance, new Object[]{});
        assertEquals(registered.size(), smartAccounts.length);
        
        //getVoters
        m = classInstance.getMethod("getVoters", new Class[]{});
        voters = (Set<SmartAccount>) m.invoke(instance, new Object[]{});
        assertEquals(voters.size(), 3);        
    }
}
