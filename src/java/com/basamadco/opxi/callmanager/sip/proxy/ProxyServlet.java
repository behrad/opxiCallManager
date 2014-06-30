package com.basamadco.opxi.callmanager.sip.proxy;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.call.LegState;
import com.basamadco.opxi.callmanager.call.UASLeg;
import com.basamadco.opxi.callmanager.sip.CallServlet;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * OpxiCallManager Core SIP Proxy Controller<br>
 * This class implements a simple proxy service which will
 * lookup OpxiCallManager service locator for target locations.<br>
 * Any specific Proxy implementations should extend this class and
 * override <i>doInvite</i> method to do their own logic.
 *
 * @author Jrad
 * @version 0.1
 */
public class ProxyServlet extends CallServlet {

    private static final Logger logger = Logger.getLogger( ProxyServlet.class.getName() );


    /**
     * Simply proxies an Invite to exists registration bindings for
     * the request-uri specified by the request.
     */
    protected void doInitialInvite( SipServletRequest request ) throws ServletException, IOException {
        try {
            request.getSession().setHandler( getServletName() );
            CallService call = getCallService( request.getSession() );

            /*request.removeHeader( REMOTE_PARTY_ID );  // Added to check IVR health receiving non-expected characters
            ((SipURI)request.getFrom().getURI()).setUser(
                    correctSipCallerId( ((SipURI)request.getFrom().getURI()).getUser() ) );*/

            initialProxy( request, call.getProxyTargets() );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            sendErrorResponse( request, e );
            getLeg( request.getSession() ).setState( LegState.IDLE );
        }
    }

    protected void doCancel( SipServletRequest cancel ) throws ServletException, IOException {
        getLeg( cancel.getSession() ).getCallService().setState( CallService.CANCELED );
//        ((UASLeg) getLeg( cancel.getSession() )).canceled();
//        getLeg( cancel.getSession() ).setState( LegState.IDLE );
    }

    protected void doBye( SipServletRequest bye ) throws ServletException, IOException {
        CallService call = getLeg( bye.getSession() ).getCallService();
        call.setDisconnectTime( System.currentTimeMillis() );
        call.setState( CallService.TERMINATED );
        try {
            if ( call.getTarget() != null ) {
                if ( call.getTarget().isApplication() ) {
                    getServiceFactory().getLogService().getServiceActivityLogger().
                            getOrAddApplicationServiceLogger( call.getTarget().getName() ).addServiceTime( call.duration() );
                }
            }

            if ( call.getTarget().isTrunk() ) {

                getServiceFactory().getLogService().getServiceActivityLogger().
                        getOrAddTrunkActivityLogger( call ).logTrunkUsage( call );

                String aor = ((SipURI) call.getCallerAddress().getURI()).toString(); // should be an Agent name

                String id = getServiceFactory().getAgentService().getAgentByAOR( aor ).getActivityLogId();
                getServiceFactory().getLogService().getAgentActivityLogger( id ).addTrunkUsage( call );

            }
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }


    protected void doRefer( SipServletRequest refer ) throws ServletException, IOException {
        logger.finest( "proxy doRefer is called to change the referred by header..." );
        // TODO add transfer service logs?!
        refer.getAddressHeader( REFERRED_BY ).setParameter( TRANSFER_CALLID_PARAM, refer.getSession().getCallId() );
    }

    protected void doAck( SipServletRequest ack ) throws ServletException, IOException {
        Leg leg = getLeg( ack.getSession() );
        leg.setState( LegState.IN_CALL );
        CallService call = leg.getCallService();
//        call.connected();
        call.setState( CallService.CONNECTED );
        call.setConnectTime( System.currentTimeMillis() );
    }

    protected void doSuccessResponse( SipServletResponse ok ) {
        if ( ok.getMethod().equals( BYE ) ) {
            getLeg( ok.getSession() ).setState( LegState.IDLE );
        }
    }

    protected void doErrorResponse( SipServletResponse error ) throws ServletException, IOException {
        Leg myleg = getLeg( error.getSession() );
        if ( error.getMethod().equals( INVITE ) ) {
            if ( myleg.getCallService().getState() != CallService.CONNECTED ) {
                myleg.getCallService().setState( CallService.TERMINATED );
                myleg.setState( LegState.IDLE );
            }
        } else { // TODO should all error responses to other requests(e.g. Subscribe) terminate the call???
            try {
                myleg.getCallService().setState( CallService.TERMINATED );
                myleg.setState( LegState.IDLE );
            }
            catch ( IllegalStateException e ) {
                // this exception occured when u want to termainate call that terminated before and the container can't
                // determine the dialog for this message. 
                logger.warning( "This call terminated before." );
            }
        }
    }

    protected void doProvisionalResponse( SipServletResponse response ) throws ServletException, IOException {
        if ( response.getStatus() == SipServletResponse.SC_RINGING ) {
            CallService call = getLeg( response.getSession() ).getCallService();
            call.setState( CallService.RINGING );
            call.setRingingTime( System.currentTimeMillis() );
        }
    }


    protected void doNotify( SipServletRequest norify ) throws ServletException, IOException {
        if ( norify.getHeader( SUBSCRIPTION_STATE ) == null ) {
            // TODO header should be constructed within call status...
            norify.setHeader( SUBSCRIPTION_STATE, "active;expires=60" );
        }
    }


}