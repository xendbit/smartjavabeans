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
