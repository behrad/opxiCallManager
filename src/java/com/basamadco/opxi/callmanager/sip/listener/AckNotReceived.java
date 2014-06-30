package com.basamadco.opxi.callmanager.sip.listener;

import com.basamadco.opxi.callmanager.sip.CallServlet;
import com.basamadco.opxi.callmanager.call.LegState;

import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is Call Manager's JSR116 based error listener to capture failtures of 2xx ACKs
 *
 * @author Jrad
 *         Date: Jul 23, 2006
 *         Time: 7:39:51 PM
 */
public class AckNotReceived extends CallServlet implements SipErrorListener {

    private static final Logger logger = Logger.getLogger( AckNotReceived.class.getName() );

    /**
     * Sends a BYE to the dialog whose 2xx has not received an ACK and sets the leg's
     * state to IDLE so call manager can clean that leg
     * @param sipErrorEvent
     */
    public void noAckReceived( SipErrorEvent sipErrorEvent ) {
        SipServletRequest invite = sipErrorEvent.getRequest();
        SipServletResponse success200 = sipErrorEvent.getResponse();
        logger.severe( "No ACK received for message: " + success200 );
        try {
            // TODO no reason to send BYE here!
            invite.getSession().createRequest( BYE ).send();
            getLeg( invite.getSession() ).setState( LegState.IDLE ); // by this we're not gonna handle 200/BYE from UAC
        } catch ( Throwable e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public void noPrackReceived( SipErrorEvent sipErrorEvent ) {
    }

}