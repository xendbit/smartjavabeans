/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.smartcontract.api.blockchain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import ng.com.idempotent.CompiledCode;
import ng.com.idempotent.CustomOIS;
import ng.com.idempotent.DynamicClassLoader;
import static ng.com.idempotent.Compiler.compile;

import org.junit.After;
import org.junit.AfterClass;
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

}
