package ng.com.idempotent.hellosmartbean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import nxt.Account;
import nxt.Attachment;

import nxt.smartcontract.api.SmartAccount;
import nxt.smartcontract.api.SmartBean;
import nxt.smartcontract.api.blockchain.request.ParameterParser;
import nxt.smartcontract.api.utils.Constants;
import nxt.smartcontract.api.blockchain.request.SmartRequest;
import nxt.smartcontract.api.blockchain.request.SmartTransaction;
import org.json.simple.JSONStreamAware;

public class HelloSJBWorld extends SmartTransaction implements SmartBean {
    
    public static final long serialVersionUID = 1;
    int version = 1;

    private String greeting = "Hello Smart Java Beans World";
    private SmartAccount account = new SmartAccount();
    private String baseURL;

    public void setAccount(SmartAccount account) {
        this.account = account;
    }

    public SmartAccount getAccount() {
        return account;
    }

    public String getHello() {
        return "Hello SJB World";
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getGreeting() {
        return greeting;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    private final String USER_AGENT = "Mozilla/5.0";

    public void sendGET(String getUrl) throws IOException {
        URL obj = new URL(getUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("GET request not worked");
        }

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
        return processResponse(json);
    }

    public String sendMessage(SmartAccount sender, SmartAccount recipient, String message) throws Exception {
        SmartRequest smartRequest = new SmartRequest(Attachment.ARBITRARY_MESSAGE);
        smartRequest.putParameter("recipient", recipient.getAddress());
        smartRequest.putParameter("message", message);
        smartRequest.putParameter("secretPhrase", sender.getPassphrase());
        smartRequest.putParameter("feeNQT", Constants.ONE_NXT + "");
        smartRequest.putParameter("deadline", "6");

        long recipientId = ParameterParser.getAccountId(smartRequest, "recipient", false);
        Account senderAccount = ParameterParser.getSenderAccount(smartRequest);
        
        JSONStreamAware json = createTransaction(smartRequest, senderAccount, recipientId, 0, smartRequest.getAttachmentType());
        return processResponse(json);
    }    
    
    public String processResponse(JSONStreamAware json) throws Exception {
        StringWriter stringWriter = new StringWriter();
        json.writeJSONString(stringWriter);
        return stringWriter.getBuffer().toString();
    }      
}
