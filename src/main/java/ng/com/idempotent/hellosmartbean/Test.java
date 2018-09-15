/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.com.idempotent.hellosmartbean;

import java.util.Arrays;
import nxt.smartcontract.api.listener.TransactionListener;

/**
 *
 * @author aardvocate
 */
public class Test {
    public static void main(String[] args) {        
        Class senderListerClass = SenderListener.class;
        
        Arrays.asList(senderListerClass.getInterfaces()).forEach(x -> {
            System.err.println(x.getName().equals(TransactionListener.class.getName()));
        });
    }
}
