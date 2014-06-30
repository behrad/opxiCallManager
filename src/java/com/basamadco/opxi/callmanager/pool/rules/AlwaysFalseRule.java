package com.basamadco.opxi.callmanager.pool.rules;

import javax.servlet.sip.SipServletRequest;

/**
 * @author Jrad
 *         Date: Oct 28, 2006
 *         Time: 4:09:40 PM
 */
public class AlwaysFalseRule extends AbstractMatchingRule {

    /**
     * Default implementation so this can be used as a place holder matching rule.
     * This implementation returns false for any requests
     *
     * @param request The SIP request for incomming call to be evaluated
     * @return false everytime
     */
    public boolean evaluate( SipServletRequest request ) {
        return false;
    }


    public String getRuleInfo() {
        return "Always false rule!";
    }
    
}