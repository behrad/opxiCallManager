package com.basamadco.opxi.callmanager.sip.front;

import java.util.logging.Logger;import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Jul 25, 2006
 *         Time: 12:21:39 PM
 */
public class MediaFront extends Front {

    private static final Logger logger = Logger.getLogger( MediaFront.class.getName() );


    protected void doInitialInvite( SipServletRequest invite ) throws ServletException, IOException {
        sendErrorResponse( invite, new IllegalStateException( "Media Service Under Construction" ) );
//        getCallService( invite.getSession() ).addUASLeg( invite, Leg.CALLER );
//        doMediaPlayBack( invite );
    }

}
