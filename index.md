#Smart Java Beans

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

#####General Overview
![SJB Overview](https://raw.githubusercontent.com/segun/HelloSmartBean/master/SmartJavaBeans.png)

#####Sample Documentation

`
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
    },{
    "byteCode": "yv66vgAAADQAbgoAGwA5BwA6CgACADkJABoAOwkAGgA8BwA9CgAGADkJABoAPgsAPwBACgAGAEEKAAYAQgcAQwoADABECQBFAEYHAEcKAA8AOQgASAoADwBJCgBKAEsIAEwKAA8ATQoATgBPCgAMAFAKAAYAUQkAGgBSBwBTBwBUBwBVAQAHdmVyc2lvbgEAAUkBAApyZWdpc3RlcmVkAQAPTGphdmEvdXRpbC9TZXQ7AQAJU2lnbmF0dXJlAQA1TGphdmEvdXRpbC9TZXQ8TG54dC9zbWFydGNvbnRyYWN0L2FwaS9TbWFydEFjY291bnQ7PjsBAAZ2b3RlcnMBAAV2b3RlZAEAE0xqYXZhL3V0aWwvSGFzaE1hcDsBAExMamF2YS91dGlsL0hhc2hNYXA8TG54dC9zbWFydGNvbnRyYWN0L2FwaS9TbWFydEFjY291bnQ7TGphdmEvbGFuZy9Cb29sZWFuOz47AQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEACWdldFZvdGVycwEAESgpTGphdmEvdXRpbC9TZXQ7AQA3KClMamF2YS91dGlsL1NldDxMbnh0L3NtYXJ0Y29udHJhY3QvYXBpL1NtYXJ0QWNjb3VudDs+OwEADWdldFJlZ2lzdGVyZWQBAA1yZWdpc3RlclZvdGVyAQAnKExueHQvc21hcnRjb250cmFjdC9hcGkvU21hcnRBY2NvdW50OylWAQAEdm90ZQEADVN0YWNrTWFwVGFibGUBAApnZXRWZXJzaW9uAQADKClJAQAKc2V0VmVyc2lvbgEABChJKVYBAApTb3VyY2VGaWxlAQAPVm90aW5nQmVhbi5qYXZhDAAnACgBABFqYXZhL3V0aWwvSGFzaFNldAwAHwAgDAAjACABABFqYXZhL3V0aWwvSGFzaE1hcAwAJAAlBwBWDABXAFgMAFkAWAwAWgBbAQARamF2YS9sYW5nL0Jvb2xlYW4MAFwAXQcAXgwAXwBgAQAXamF2YS9sYW5nL1N0cmluZ0J1aWxkZXIBAAZWb3RlciAMAGEAYgcAYwwAZABlAQAdIHRyaWVkIHRvIHZvdGUgbW9yZSB0aGFuIG9uY2UMAGYAZQcAZwwAaABpDABqAGsMAGwAbQwAHQAeAQArbmcvY29tL2lkZW1wb3RlbnQvaGVsbG9zbWFydGJlYW4vVm90aW5nQmVhbgEAEGphdmEvbGFuZy9PYmplY3QBAB9ueHQvc21hcnRjb250cmFjdC9hcGkvU21hcnRCZWFuAQANamF2YS91dGlsL1NldAEAA2FkZAEAFShMamF2YS9sYW5nL09iamVjdDspWgEAC2NvbnRhaW5zS2V5AQADZ2V0AQAmKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsBAAxib29sZWFuVmFsdWUBAAMoKVoBABBqYXZhL2xhbmcvU3lzdGVtAQADZXJyAQAVTGphdmEvaW8vUHJpbnRTdHJlYW07AQAGYXBwZW5kAQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7AQAibnh0L3NtYXJ0Y29udHJhY3QvYXBpL1NtYXJ0QWNjb3VudAEACmdldEFkZHJlc3MBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEACHRvU3RyaW5nAQATamF2YS9pby9QcmludFN0cmVhbQEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAd2YWx1ZU9mAQAWKFopTGphdmEvbGFuZy9Cb29sZWFuOwEAA3B1dAEAOChMamF2YS9sYW5nL09iamVjdDtMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7ACEAGgAbAAEAHAAEAAIAHQAeAAAAEgAfACAAAQAhAAAAAgAiABIAIwAgAAEAIQAAAAIAIgASACQAJQABACEAAAACACYABwABACcAKAABACkAAABKAAMAAQAAACYqtwABKrsAAlm3AAO1AAQquwACWbcAA7UABSq7AAZZtwAHtQAIsQAAAAEAKgAAABIABAAAABMABAAVAA8AFgAaABcAAQArACwAAgApAAAAHQABAAEAAAAFKrQABbAAAAABACoAAAAGAAEAAAAaACEAAAACAC0AAQAuACwAAgApAAAAHQABAAEAAAAFKrQABLAAAAABACoAAAAGAAEAAAAeACEAAAACAC0AAQAvADAAAQApAAAAKAACAAIAAAAMKrQABCu5AAkCAFexAAAAAQAqAAAACgACAAAAIgALACMAAQAxADAAAQApAAAAhwADAAIAAABWKrQACCu2AAqZADUqtAAIK7YAC8AADLYADZkAJLIADrsAD1m3ABASEbYAEiu2ABO2ABISFLYAErYAFbYAFiq0AAgrBLgAF7YAGFcqtAAFK7kACQIAV7EAAAACACoAAAAWAAUAAAAnABwALAA9AC4ASgAvAFUAMAAyAAAAAwABPQABADMANAABACkAAAAdAAEAAQAAAAUqtAAZrAAAAAEAKgAAAAYAAQAAADQAAQA1ADYAAQApAAAAIgACAAIAAAAGKhu1ABmxAAAAAQAqAAAACgACAAAAOQAFADoAAQA3AAAAAgA4"
}`