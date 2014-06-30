package com.basamadco.opxi.callmanager.sip.front;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.call.UASLeg;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 17, 2006
 *         Time: 10:56:32 AM
 *         <p/>
 *         <p/>
 *         Handles Initial Invites received by greeting REFERs from us.
 */
public class GreetingFront extends Front {

    private static final Logger logger = Logger.getLogger( GreetingFront.class.getName() );


    protected void doInitialInvite( SipServletRequest invite ) throws ServletException, IOException {
//        try {
        if ( CANCEL.equals( invite.getSession().getAttribute( "winner_invite_cancel_race" ) ) ) {
            logger.warning( "This INVITE is already canceled!!! :o " );
            // ignore INVITE since it is canceled before :D
            return;
        }
        Address referredBy = invite.getAddressHeader( REFERRED_BY );
        if ( referredBy == null ) {
            invite.createResponse( SipServletResponse.SC_BAD_REQUEST, "Missing Referred-By Header" ).send();
            return;
        }
        String transfereeCallId = referredBy.getParameter( TRANSFER_CALLID_PARAM );
        CallService call = getServiceFactory().getQueueManagementService().
                getExpectedCallForTransfer( transfereeCallId );
        if ( call == null ) {
            invite.createResponse( SipServletResponse.SC_BUSY_HERE, "Transfer Not Expected." ).send();
        } else {
            if ( call.getTransferTimer() != null ) {
                call.getTransferTimer().cancel();
            }

            // Added in 6133 to postpone basic leg terminations in transfers from waiting room
            Leg caller = call.getLeg( Leg.CALLER );
            caller.terminate();
            caller.getPeer().terminate();
            // ---------------------------

            try {
                // this ensures that call controller will use this sipsession as UAS not the original caller
                call.setTransferLeg( call.addUASLeg( invite, Leg.TRANSFERED_TO_GREETING ) );


                if ( !getServiceFactory().getQueueManagementService().queueForName( call.getHandlerQueueName() )
                        .doesApplicationInvolve( call ) ) { // check for ASSIGNED state applications
                    call.playGreeting();
                }
//                    call.playGreeting();


            } catch ( OpxiException e ) {
                logger.severe( e.getMessage() );
                ((UASLeg) getLeg( invite.getSession() )).reject(
                        SipServletResponse.SC_SERVER_INTERNAL_ERROR, e.getMessage()
                );
            }
        }
//        } catch ( OpxiException e ) {
//            logger.log( Level.SEVERE, e.getMessage(), e );
//        }
    }

    protected void doCancel( SipServletRequest req ) throws ServletException, IOException {
        req.getSession().setAttribute( "winner_invite_cancel_race", CANCEL );
    }

}
