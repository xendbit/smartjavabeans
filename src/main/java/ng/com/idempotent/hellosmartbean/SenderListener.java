/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.com.idempotent.hellosmartbean;

import nxt.Transaction;
import nxt.smartcontract.api.SmartAccount;
import nxt.smartcontract.api.SmartBean;
import nxt.smartcontract.api.blockchain.request.SmartTransaction;
import nxt.smartcontract.api.listener.TransactionListener;
import nxt.util.Logger;

/**
 *
 * @author aardvocate
 */
public class SenderListener extends SmartTransaction implements TransactionListener, SmartBean {
    int version = 1;

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public boolean findTransaction(Transaction incoming) {
        return incoming.getSenderId() == new SmartAccount("copper explain fated truck neat unite branch educated tenuous hum decisive notice").getAccountId();
    }

    @Override
    public void onTransactionFound(Transaction t) {
        Logger.logInfoMessage("Yay!!! Listener is working");
    }
}
