# Smart Java Beans

The NXT platform is a powerful blockchain platform. The only down-side is there are no smart contracts. Our aim is to be able to implement Smart Contracts on the NXT blockchain. We call these Smart Contracts **Smart Java Beans** (or **SJB** for short).

***The design considerations are as follows.***

1. SJBs are normal java beans that can be run on the NXT blockchain.
2. SJBs implement Serializable so that their states can be stored on the blockchain.
3. There's a helper interface called `Contract`. All SJBs must implement this interface and provide implementations for all its methods.
4. SJBs must have access to all the APIs exposed by the NXT platform.
5. SJBs must follow the below naming conventions
	1.	Methods that must return a value **MUST** begin with get, e.g `public long getAccountBalance()`. These types of methods cost 0 to execute and doesn't affect the state of the SJB on the blockchain
	2.	Methods that have side effects must return void. e.g `public void setAccountBalance(int long)`. After the execution of this method, the state of the SJB object will be altered.
	3. SJBs must provide a setter and getter for a variable named `version`, the type of `version` is int. This is enforced by the `Contract` interface.

We need a custom dynamic class loader, that could load classes into memory from bytecode. We also need a custom ObjectInputStream that can use the Dynamic class loader. All these is put together in this project [InMemoryJavaCompiler](https://github.com/segun/InMemoryJavaCompiler) to compile java source file into bytecode. After compilation, the InMemory compiler returns the bytecode and a documentation (json) generated from the source file (sample included below)

After compilation, the bytecode and documentation are sent in an API call to the blockchain. The API will store us the Dynamic Class Loader to load the bytecode, then an instance is created. The Custom ObjectInputStream is used to serialize the instance. A base64 encoded String of both the bytecode and the serialized instance are stored in the DB along with the documentation.

The API also include a call that will return the documentation so that the developer can know the methods and return types of the methods.

When the API call to execute the smart contract is made, the Dynamic Class Loader loads the bytecode from memory, the custom ObjectInputStream loads the instance (Using the class loader) and using reflections, the proper method and variables are passed to the instance.

If the call is a set method, the new instance is serialized once again and put on the blockchain.

##### General Overview
![SJB Overview](https://raw.githubusercontent.com/segun/HelloSmartBean/master/sjb_overview.png)

##### Sample Documentation

```
    "documentation": {
        "methods": [{
                "name": "getVersion",
                "parameters": [],
                "returnType": "int"
            }, {
                "name": "registerVoter",
                "parameters": ["nxt.smartcontract.api.SmartAccount"],
                "returnType": "void"
            }, {
                "name": "getRegistered",
                "parameters": [],
                "returnType": "java.util.Set"
            }, {
                "name": "vote",
                "parameters": ["nxt.smartcontract.api.SmartAccount"],
                "returnType": "void"
            }, {
                "name": "getVoters",
                "parameters": [],
                "returnType": "java.util.Set"
            }, {
                "name": "setVersion",
                "parameters": ["int"],
                "returnType": "void"
            }],
        "name": "ng.com.idempotent.hellosmartbean.VotingBean"
    }
```

## Examples

### Example one: SimpleBean.

```
import nxt.smartcontract.api.SmartBean;

public class SimpleSJB implements SmartBean {

    int version;
    
    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }    
}
```

### Example two: Sending money from one account to another.
This type of bean must extend SmartTransaction because it will perform a transaction on the blockchain.

```
package ng.com.idempotent.hellosmartbean;

import java.io.StringWriter;
import nxt.Account;
import nxt.Attachment;
import nxt.smartcontract.api.SmartAccount;
import nxt.smartcontract.api.SmartBean;
import nxt.smartcontract.api.blockchain.request.ParameterParser;
import nxt.smartcontract.api.blockchain.request.SmartRequest;
import nxt.smartcontract.api.blockchain.request.SmartTransaction;
import nxt.smartcontract.api.utils.Constants;
import org.json.simple.JSONStreamAware;

/**
 *
 * @author aardvocate
 */
public class SimpleSJB extends SmartTransaction implements SmartBean {

    int version;

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    public String sendMoney(SmartAccount sender, SmartAccount recipient, Long amountNQT) throws Exception {
        SmartRequest smartRequest = new SmartRequest(Attachment.ORDINARY_PAYMENT);
        smartRequest.putParameter("recipient", recipient.getAddress());
        smartRequest.putParameter("secretPhrase", sender.getPassphrase());
        smartRequest.putParameter("feeNQT", Constants.ONE_NXT + "");
        smartRequest.putParameter("deadline", "6");

        long recipientId = ParameterParser.getAccountId(smartRequest, "recipient", false);
        Account senderAccount = ParameterParser.getSenderAccount(smartRequest);
        
        JSONStreamAware json = createTransaction(smartRequest, senderAccount, recipientId, amountNQT);
        
        StringWriter stringWriter = new StringWriter();
        json.writeJSONString(stringWriter);
        return stringWriter.getBuffer().toString();
    }    
}
```

### Example 3: pushing the SJB to the blockchain

```
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
``` 

### Example 4: executing smart contract 

```
    public void testExecuteSendMoney() throws Exception {
        System.out.println("testExecuteSendMoney");
        //account that owns the smart contract
        SmartAccount owner = new SmartAccount(1565770067262084023L);
        long id = 7241189096096398119L;
        //this is the account calling the smart contract. This account will bear the fees
        SmartAccount executingAccount = new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice");
        
        SmartAccount sender = new SmartAccount("Baba fi owo kan idodo omo oni dodo ni dodo ilu wa");
        SmartAccount recipient = new SmartAccount(2595880067262094023L);
        
        String fullyQualifiedClassName = "ng.com.idempotent.hellosmartbean.HelloSJBWorld";        
        
        SmartMethod smartMethod = new SmartMethod("sendMoney");
        smartMethod.setParameterTypes(SmartAccount.class, SmartAccount.class, Long.class);
        smartMethod.setParameterValues(sender, recipient, (100 * Constants.ONE_NXT));
        SmartClass smartClass = new SmartClass(owner, id, fullyQualifiedClassName);
        smartClass.setSmartMethod(smartMethod);
        
        String result = SmartCall.executeSmartContract(smartClass, executingAccount);
        HashMap resultMap = new ObjectMapper().readValue(result, HashMap.class);
        assertTrue(resultMap.containsKey("transactionJSON"));
        assertTrue(resultMap.containsKey("transaction"));
    }        
```

Check the src folder and test folder for more examples.