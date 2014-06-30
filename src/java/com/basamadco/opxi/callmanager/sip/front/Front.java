package com.basamadco.opxi.callmanager.sip.front;

import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

import com.basamadco.opxi.callmanager.sip.CallServlet;

/**
 * OpxiCallManager Front controller of which methods are invoked once per an incomming INVITE.
 * <p>
 * This servlet handles all INVITE request dispaching issues.
 * </p>
 * @author Jrad
 *
 */
public class Front extends CallServlet {

    private static final Logger logger = Logger.getLogger( Front.class.getName() );

    protected void doInvite( SipServletRequest request ) throws ServletException, IOException {
        if( request.isInitial() ) {
//            CallService newCall = new CallService( request, Leg.CALLER, getSipContext() );                        
//            setSessionCall( request.getSession(), newCall );
        }
        super.doInvite( request );
    }

}