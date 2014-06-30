package com.basamadco.opxi.callmanager.sip.front;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;

import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;

/**
 * @author Jrad
 *         Date: Aug 27, 2006
 *         Time: 2:42:17 PM
 */
public class OutboundCallFront extends Front {

    private static final Logger logger = Logger.getLogger( OutboundCallFront.class.getName() );

    protected void doInitialInvite( SipServletRequest request ) throws OpxiException, ServletException, IOException {
        logger.finer( "Route this call to gateway!" );
        CallService outboundCall = new com.basamadco.opxi.callmanager.call.OutboundCall( request, Leg.CALLER, getServiceFactory().getSipService() );
        ((com.basamadco.opxi.callmanager.call.UASLeg)outboundCall.getLeg( request.getSession() )).reject( 503, "Outbound Call Service Not Available" );
    }

}
