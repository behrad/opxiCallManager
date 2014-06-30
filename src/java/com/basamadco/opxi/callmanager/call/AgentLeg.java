package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.SipService;

import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipServletRequest;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Aug 1, 2009
 *         Time: 3:11:19 PM
 */
public class AgentLeg extends UACLeg {

    private final static Logger logger = Logger.getLogger( AgentLeg.class.getName() );


    public AgentLeg( SipService sipFactory, CallService call, SipServletRequest req, String roleName ) {
        super( sipFactory, call, req, roleName );
    }


}
