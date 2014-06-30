package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.SipService;

import javax.servlet.sip.SipServletRequest;

/**
 * @author Jrad
 *         Date: May 2, 2010
 *         Time: 2:52:59 PM
 */
public class TransferedCall extends CallService {


    public TransferedCall( SipServletRequest request, String role, SipService ctx ) {
        super( request, role, ctx );
    }


}
