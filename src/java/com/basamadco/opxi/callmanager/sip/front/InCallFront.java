package com.basamadco.opxi.callmanager.sip.front;

import com.basamadco.opxi.callmanager.LocationService;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.*;
import com.basamadco.opxi.callmanager.call.route.CallRouteMatchingException;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.sip.MediaServerNotRegistered;
import com.basamadco.opxi.callmanager.sip.NoLegAttributeIsSetException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 4, 2006
 *         Time: 2:43:06 PM
 */
public class InCallFront extends Front {

    private final static Logger logger = Logger.getLogger( InCallFront.class.getName() );


    protected void doInitialInvite( SipServletRequest request ) throws ServletException, IOException {
        CallService call = null;
        LocationService ls = getServiceFactory().getLocationService();
        try {

            CallService toBeTransfered = preprocess( request );

            logger.finer( "Trying to resolve call target for callId = " + request.getCallId() );
            CallTarget target = getServiceFactory().getRoutingService().route( request );

            logger.info( "Call target resolved: " + target );
            target.setServiceFactory( getServiceFactory() );
            target.setRequest( request );

            if ( toBeTransfered != null ) {
                call = new TransferedCall( request, Leg.CALLER, getServiceFactory().getSipService() );
                call.setIM( toBeTransfered.getIM() );
            } else if ( target instanceof Trunk ) {
                call = new OutboundCall( request, Leg.CALLER, getServiceFactory().getSipService() );
            } else if ( getServiceFactory().getLocationService().isRegisteredContact( request.getAddressHeader( CONTACT ) ) ) {
                call = new LocalCall( request, Leg.CALLER, getServiceFactory().getSipService() );
            } else {
                call = new InboundCall( request, Leg.CALLER, getServiceFactory().getSipService() );
            }

            call.setTarget( target );

            target.service( call );

        }
        catch ( MediaServerNotRegistered e ) {
            logger.warning( "Try (re)registering media server please: " + e.getMessage() );
            /*getServiceFactory().getSipService().sendAdminIM(
                    ResourceBundleUtil.getMessage( "callmanager.admin.ivr.unavailable" )
            );
            getServiceFactory().getSipService().sendAdminIM(
                    ResourceBundleUtil.getMessage( "callmanager.admin.ivr.reRegister" )
            );*/
            request.createResponse( SipServletResponse.SC_NOT_FOUND, "Media Server Not Found" ).send();
            getLeg( request.getSession() ).setState( LegState.IDLE ); // This is expected to work
        } catch ( CallLoopException e ) {
            request.createResponse( SipServletResponse.SC_LOOP_DETECTED ).send();
            getLeg( request.getSession() ).setState( LegState.IDLE ); // This is expected to work
        } catch ( CallRouteMatchingException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            request.createResponse( SipServletResponse.SC_NOT_FOUND, e.getMessage() ).send();
            try {
                getServiceFactory().getLogService().getServiceActivityLogger().
                        addUnsuccessfulHandling( "Call Target Not Found", "Please check log for callee information." );
            } catch ( ActivityLogNotExistsException e1 ) {
                logger.log( Level.SEVERE, e1.getMessage(), e1 );
            }
        } catch ( Throwable e ) {
            logger.log( Level.SEVERE, "Couldn't handle incomming call request: " + e.getMessage(), e );
            sendErrorResponse( request, e );
            try {
                getServiceFactory().getLogService().getServiceActivityLogger().
                        addUnsuccessfulHandling( request.getRequestURI().toString(),
                                OpxiToolBox.unqualifiedClassName( e.getClass() ) + ": " + e.getMessage() );
            } catch ( ActivityLogNotExistsException e1 ) {
                logger.log( Level.SEVERE, e1.getMessage(), e1 );
            }
            try {
                logger.finest( "Trying to set leg state to IDLE..." );
                getLeg( request.getSession() ).setState( LegState.IDLE );
            } catch ( NoLegAttributeIsSetException e1 ) {
                logger.warning( "No leg was set in this sipSession!" );
            }
        }
    }

    private CallService preprocess( SipServletRequest request ) throws OpxiException, ServletException {
        /**
         * TODO: Check if transfer INVITEs work fine this way
         *
         */
        Address referredBy = request.getAddressHeader( REFERRED_BY );
        CallService call = null;
        if ( referredBy != null ) {  //Check if incomming INVITE has a ReferredBy header
            String transfereeCallId = referredBy.getParameter( TRANSFER_CALLID_PARAM );


            /*if ( getServiceFactory().getLocationService().isRegisteredContact( request.getTo() ) ) {

            }*/


            if ( transfereeCallId != null ) {
                call = getServiceFactory().getQueueManagementService().getExpectedCallForTransfer( transfereeCallId );
                if ( call != null ) {
                    logger.finest( "********************** WOW: this call transfer was expected." );
                } else {

                    try {
                        call = CallServiceFactory.getCallService( transfereeCallId );
                        if ( call.getTransferTimer() != null ) {
                            call.getTransferTimer().cancel();
                        }
                    } catch ( CallNotExistsException e ) {
                        logger.warning( "Canceling transfer timer: call not exists." );
                    }
//                        call.setTransferLeg( call.addUASLeg( request, Leg.TRANSFERED_LEG ) );
                }
            } else {
                logger.finest( "Un-supervised call transfer message..." );
                /*request.createResponse( SipServletResponse.SC_NOT_ACCEPTABLE, "Invalid Referred By Header" ).send();
                return;*/
            }
        }
        return call;
    }


}