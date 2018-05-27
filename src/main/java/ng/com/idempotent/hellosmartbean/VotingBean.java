/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ng.com.idempotent.hellosmartbean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import nxt.smartcontract.api.SmartAccount;
import nxt.smartcontract.api.SmartBean;

/**
 *
 * @author aardvocate
 */
@SuppressWarnings("unchecked")
public class VotingBean implements SmartBean {
    private int version;
    private final Set<SmartAccount> registered = new HashSet<>();
    private final Set<SmartAccount> voters = new HashSet<>();    
    private final HashMap<SmartAccount, Boolean> voted = new HashMap();

    public Set<SmartAccount> getVoters() {
        return voters;
    }

    public Set<SmartAccount> getRegistered() {
        return registered;
    }
    
    public void registerVoter(SmartAccount voter) {
        registered.add(voter);
    }
    
    public void vote(SmartAccount voter) {
        
        if(voted.containsKey(voter) && voted.get(voter)) {
            /**
             * Voter is trying to vote twice. We use a Set so this is not a problem
             * But then let's log it.
            */
            System.err.println("Voter " + voter.getAddress() + " tried to vote more than once");
        }
        voted.put(voter, true);
        voters.add(voter);
    }
        
    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }        
}
