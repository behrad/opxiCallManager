package com.basamadco.opxi.callmanager.sip;

import javax.servlet.sip.SipServletRequest;

/**
 * @author Jrad
 *         Date: Nov 29, 2007
 *         Time: 1:05:51 PM
 */
public interface RequestModifier {

    public void modify( SipServletRequest request );

}
