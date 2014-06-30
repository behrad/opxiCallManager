package com.basamadco.opxi.callmanager.pool.rules;

import javax.servlet.sip.SipServletRequest;

/**
 * Opxi Call Manager rule model used for matching skills with incomming SIP calls
 *
 * @author Jrad
 *         Date: Oct 28, 2006
 *         Time: 3:03:03 PM
 */
public interface MatchingRule {

    /**
     * Specific subclasses should implement this to handle their matching logic
     *
     * @param request The SipServletRequest representing the incomming SIP call to be being matched with
     * @return True if this matching rule evaluates to true for the input request
     */
    public boolean evaluate( SipServletRequest request );

    /**
     * Should return a description of this matching rule logic
     *
     * @return MatchingRule description string
     */
    public String getRuleInfo();

}