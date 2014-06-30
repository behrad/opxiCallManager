package com.basamadco.opxi.callmanager.sip.b2bua.greeting;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.call.LegState;
import com.basamadco.opxi.callmanager.call.UASLeg;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.sip.MediaServerNotRegistered;
import com.basamadco.opxi.callmanager.sip.b2bua.GenericB2BUA;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 15, 2006
 *         Time: 11:27:41 AM
 */
public class GreetingServlet extends GenericB2BUA {

    private static final Logger logger = Logger.getLogger( GreetingServlet.class.getName() );


    protected URI getCalleeURI( SipServletRequest request ) throws OpxiException, ServletException {
        /*  CallService call = getCallService( request.getSession() );
        Agent agent = getServiceFactory().getPoolService().getAgentByAOR( call.getHandlerAgent() );
//        String agentId = agent.getId();
        String agentId = agent.getAOR();
        String greeting = agent.getGreetingMsgURI();
//        String agentURI = agent.getAOR().replaceAll( "@", "%40" );//.replaceAll( ":", "%58" )
//        String callerId = call.getId(); //.replaceAll( "@", "%40" );
        String callerId = request.getCallId();
        String user = SipUtil.getName( agent.getAOR() );
        String pass = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getUserPassword( user );
        String ringtone = RING_TONE_URL;
        return getServiceFactory().getMediaService().getGreetingMediaURI(
                greeting, user, pass, callerId, agentId, ringtone
        );*/
        throw new IllegalStateException( "Depricated Method!!!" );
    }

    protected String getUACRoleName() {
        return Leg.GREETING_MEDIA;
    }

    protected void doCallerInvite( SipServletRequest callerInvite ) throws OpxiException, ServletException, IOException {
        try {
            super.doCallerInvite( callerInvite );
//            getCallService( callerInvite.getSession() ).setState( CallService.IN_GREETING );
        } catch ( MediaServerNotRegistered e ) {
            ((UASLeg) getLeg( callerInvite.getSession() )).reject(
                    SipServletResponse.SC_TEMPORARLY_UNAVAILABLE, e.getMessage()
            );
            // handle VOICE_APP not existance
        }
    }

    protected void doCallerAck( SipServletRequest callerAck ) throws ServletException, IOException {
        super.doCallerAck( callerAck );
        com.basamadco.opxi.callmanager.call.CallService call = getCallService( callerAck.getSession() );
        if ( call.getState() == CallService.ASSIGNED ) {
            call.setState( com.basamadco.opxi.callmanager.call.CallService.IN_GREETING );
        }
    }


    protected void doCallerBye( SipServletRequest callerBye ) throws ServletException, IOException {
        Leg callerLeg = getLeg( callerBye.getSession() );
        LegState callerStateOnRcv = callerLeg.getState();
        CallService call = callerLeg.getCallService();
        int callState = call.getState();
        super.doCallerBye( callerBye );
//        if( callState == CallService.RINGING ) { // IVR would/should? handle this
//            call.teardown( "Caller BYE when agent ringing" );
//        }
        if ( callerStateOnRcv.equals( LegState.REFRESH_INVITE_SENT ) ) { // or call.getState() == CS.TRANSFERING
            call.teardown( "Caller BYE when transfering" );
        }
    }

    protected void doCaller200ToInvite( SipServletResponse caller200 ) throws ServletException, IOException {
        SipSession caller = caller200.getSession();
        CallService call = getCallService( caller );
        if ( getLegState( caller ).equals( LegState.REFRESH_INVITE_SENT ) ) {
            // Caller answer to refresh invite
            // 1. Ack caller's answer
            Leg callerLeg = getLeg( caller );
            callerLeg.setState( LegState.IN_CALL );
            caller200.createAck().send();

            // 2. Ack agent with the caller's answer
            Leg agent = call.getLeg( Leg.AGENT );
            agent.setState( LegState.IN_CALL );
            SipServletRequest agentACK = agent.getSuccessResponse().createAck();
            copyContent( caller200, agentACK );
            agentACK.send();

            // ********************************************
            callerLeg.bind( agent );

//            setPeerSession( caller, agent.getSession() );
            // 3. terminate greeting media -> receive a CANCEL on transfer!
            Leg greetingLeg = null;
            try {
                greetingLeg = call.getLeg( Leg.GREETING_MEDIA );
                logger.finer( "Terminating Greeting Media..." );
                greetingLeg.terminate(); // This should also force a CANCEL on IVR's postgreeting leg
            } catch ( IllegalStateException e ) {
                // Greeting leg may not be still valid
                logger.warning( "greeting leg not exist: " + e.getMessage() );
            }
            // ********************************************

            callerLeg.getCallService().answered();
        } else {
            super.doCaller200ToInvite( caller200 );
        }
    }

    protected void doCallerErrorToInvite( SipServletResponse callerError ) throws ServletException, IOException {
        SipSession caller = callerError.getSession();
        Leg callerLeg = getLeg( caller );
        if ( callerLeg.getState().equals( LegState.REFRESH_INVITE_SENT ) ) {
            if ( callerError.getStatus() == SipServletResponse.SC_REQUEST_PENDING ) {
                // handle caller hold and front manager reInvite race!
                // give caller another chance!
                // TODO check loop!
                if ( callerLeg.reInviteCount <= 3 ) {
                    callerLeg.createRequest( callerError.getRequest() ).send();
                    callerLeg.reInviteCount++;
                } else {
                    callerLeg.setState( LegState.FAILURE );
                    callerLeg.getCallService().teardown( "reInvite try count exceeded" );
                }
            } else {
                super.doCallerErrorToInvite( callerError );
            }
        } else {
            super.doCallerErrorToInvite( callerError );
        }
    }

    protected void doCalleeErrorToInvite( SipServletResponse calleeError ) throws ServletException, IOException {
        super.doCalleeErrorToInvite( calleeError );
        if ( calleeError.getStatus() == SipServletResponse.SC_REQUEST_TIMEOUT ) {
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .addUnsuccessfulHandling( "Media Request Timeout", "Could not establish connection to media server" );
            } catch ( ActivityLogNotExistsException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        }
    }

    protected void doErrorToBye( SipServletResponse response ) throws IOException, ServletException {
        Leg leg = getLeg( response.getSession() );
        if ( leg.getRoleName().equals( Leg.GREETING_MEDIA )
                && response.getStatus() == SipServletResponse.SC_REQUEST_TIMEOUT ) {
            // IVR has gone away, so it would'nt teardown its postGreeting leg (if any)
            leg.getCallService().teardown( "Media Server Timeout In Greeting" );
//            try {
//                UASLeg ivr_trasferer = (UASLeg)leg.getCallService().getLeg( Leg.IVR_TRANSFER_AGENT );
//                ivr_trasferer.reject( 500, "Hey IVR! You are down." );
//                // TODO 1) terminate the callService? OR
//                // TODO 2) terminate the AgentLeg?
//            } catch ( IllegalStateException e ) {
//                logger.finer( e );
//            }
        }
    }

}
