package com.basamadco.opxi.callmanager.sip.b2bua.greeting;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.queue.QueueManagerException;
import com.basamadco.opxi.callmanager.queue.QueueNotExistsException;
import com.basamadco.opxi.callmanager.pool.NoPoolAvailableException;
import com.basamadco.opxi.callmanager.pool.AgentNotAvailableException;
import com.basamadco.opxi.callmanager.call.*;
import com.basamadco.opxi.callmanager.logging.AgentActivityLogger;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.sip.b2bua.GenericB2BUA;
import com.basamadco.opxi.callmanager.sip.SipCallController;

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
 *         Time: 11:42:17 AM
 */
public class PostGreetingServlet extends GenericB2BUA {

    private static final Logger logger = Logger.getLogger( PostGreetingServlet.class.getName() );


    protected URI getCalleeURI( SipServletRequest request ) throws OpxiException, ServletException {
/*//        String ctxId = request.getAddressHeader( TO ).getParameter( PostGreetingServlet.GREETING_CTX_ID_PARAM );
        GreetingContext ctx = (GreetingContext)request.getSession().getAttribute( GREETING_CTX_ID_PARAM );
        Agent agent =
                getServiceFactory().getPoolService().getAgentByAOR( ctx.getAgentURI() );
        logger.finer( "Post greeting resolved agent: " + agent.getAOR() );
        return agent.getContact().toString();*/
        throw new IllegalStateException( "Depricated Method!!!" );
    }

    protected String getUACRoleName() {
        return com.basamadco.opxi.callmanager.call.Leg.AGENT;
    }

    /*private final static char AMPERSAND = '&';
    private final static char EQUAL = '=';*/
    protected void doCallerInvite( SipServletRequest invite ) throws OpxiException, ServletException, IOException {
/*        try {
            SipSession callerSession = invite.getSession();
            UASLeg callerLeg = (UASLeg)getLeg( callerSession );
            CallService call = getCallService( callerSession );
            Leg callInitiator = call.getTransferLeg();
            logger.finer( "call initiator state " + callInitiator );
            if( callInitiator.getState().equals( LegState.IN_CALL ) ) {
            //            String agentId = invite.getAddressHeader( TO ).getParameter( GREETING_CTX_ID_PARAM );
            //            String agentAOR = SipUtil.toSipURIString( agentId );
                GreetingContext ctx = (GreetingContext)invite.getSession().getAttribute( GREETING_CTX_ID_PARAM );
                String agentAOR = ctx.getAgentURI();
                Address agentAddress = getSipFactory().createAddress( getSipFactory().createURI( agentAOR ),
                        SipUtil.getName( agentAOR ) *//* agentId *//*
                );
                SipServletRequest agentInvite = callerLeg.
                        createRequest( invite, call.getCallerAddress(), agentAddress );
                agentInvite.setRequestURI( getSipFactory().createURI( getCalleeURI( invite ) ) );
                agentInvite.getSession().setHandler( getServletName() );
                // "<http://opxi.basamadco.com/opxiCRM/test.jsp>;purpose=info"
                String sipuser = ((SipURI)call.getCallerAddress().getURI()).getUser();
                String displayname = call.getCallerAddress().getDisplayName();
                String domain = ((SipURI)call.getCallerAddress().getURI()).getHost();
                StringBuffer callInfo_buffer = new StringBuffer(  "/" + A2CBridge.ACTION  + "?" );
                if ( sipuser != null ) {
                    callInfo_buffer.append( AMPERSAND ).append( A2CBridge.CALLER_SIPUSERNAME_PARAMETER_NAME )
                            .append( EQUAL ).append( sipuser.replaceAll( "\\+", "%2B" ) );
                }
                if ( displayname != null) {
                    callInfo_buffer.append( AMPERSAND ).append( A2CBridge.CALLER_DISPLAYNAME_PARAMETER_NAME )
                            .append( EQUAL ).append( displayname );
                }
                if ( domain != null) {
                    callInfo_buffer.append( AMPERSAND ).append( A2CBridge.CALLER_SIPDOMAIN_PARAMETER_NAME )
                    .append( EQUAL ).append( domain );
                }

                callInfo_buffer.append( AMPERSAND ).append( ORIGINAL_TO ).append( EQUAL )
                                    .append( callInitiator.getInitialRequest().getTo().toString() );

                logger.finest( "call info: '" + callInfo_buffer + "'" );
                Address callInfo = getSipFactory().createAddress(
                        getServiceFactory().getSipService().getLocalURL( callInfo_buffer.toString() )
                );
                callInfo.setParameter( "purpose", "info" );
                agentInvite.setAddressHeader( CALL_INFO, callInfo );

                callerLeg.createPeerLeg( agentInvite, getUACRoleName() );
                agentInvite.send();
                call.getLeg( invite.getSession() ).setLegTimer(
                    new PostGreetingLegTerminationTimer( getServiceFactory(), call )
                );
                call.setState( CallService.MAKE_CALL );
            } else {
                callerLeg.reject( SipServletResponse.SC_TEMPORARLY_UNAVAILABLE, "Caller Not Available" );
            }
        } catch ( CallAlreadyDisposedError e ) {
            invite.createResponse( SipServletResponse.SC_GONE, "Call Already Disposed" ).send();
            logger.log( Level.WARNING, e.getMessage(), e );
        }*/
    }

    protected void doCallee200ToInvite( SipServletResponse agent200 ) throws ServletException, IOException {
        SipSession agent = agent200.getSession();
        Leg agentLeg = getLeg( agent );
        CallService call = agentLeg.getCallService();
        Leg caller = call.getTransferLeg();

        try {
            AgentActivityLogger aal = getServiceFactory().getLogService().getAgentActivityLogger(
                    getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() ).getActivityLogId()
            );
            aal.addAnsweredCallTime( call.getAgentAnswerTime() );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }

        if ( call.getState() == CallService.IN_GREETING ) { // i.e. the opxiCallLogger app is active
            super.doCallee200ToInvite( agent200 );
        } else {

            if ( agentLeg.getState().equals( LegState.TRYING ) ) {
                agentLeg.setState( LegState.ACK_PENDING );
                if ( caller.getState().equals( LegState.IN_CALL ) ) {
                    // 1. Re-INVITE caller
                    try {
                        SipServletRequest reInvite = caller.getSession().createRequest( INVITE );
                        copyContent( agent200, reInvite );
                        reInvite.send();
                        caller.setState( LegState.REFRESH_INVITE_SENT );
                        call.setState( CallService.TRANSFERING );
                    } catch ( IllegalStateException e ) {
                        logger.severe( "Could not reinvite caller: " + e );
                        // BAD ACK!
                        agent200.createAck().send(); // This forces a BYE from agent!!!
                        // TODO send BYE? this makes a "dead sure" 481 and an forced Exception in our app for that.
                    }
                } else { // caller is not in INCALL state
                    // BAD ACK!
                    agent200.createAck().send();
                    // NO BYE...! Agent will generate one for us
                }
            } else {
                super.doCallee200ToInvite( agent200 );
            }
        }
    }

    protected void doCalleeErrorToInvite( SipServletResponse agentError ) throws ServletException, IOException {
        SipSession agentSession = agentError.getSession();
        Leg agentLeg = getLeg( agentSession );
        CallService call = getCallService( agentSession );
        if ( agentLeg.getState().equals( LegState.FAILURE )
                || agentError.getStatus() == SipServletResponse.SC_REQUEST_TIMEOUT ) {  // request is canceled (or timeout!)
            if ( !call.isAbandoned() ) {
                try {
                    getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() ).notAnswered();
                } catch ( AgentNotAvailableException e ) {
                    logger.severe( e.getMessage() );
                }
            }
        } else if ( agentError.getStatus() == SipServletResponse.SC_TEMPORARLY_UNAVAILABLE ) {
//        call.setState( CallService.REJECTED );
            if ( !call.isAbandoned() ) {
                try {
                    getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() ).rejected( call );
                } catch ( AgentNotAvailableException e ) {
                    logger.severe( e.getMessage() );
                }
            }
        }
        super.doCalleeErrorToInvite( agentError );
    }

    @Override
    protected void doCallerAck( SipServletRequest callerAck ) throws ServletException, IOException {
        super.doCallerAck( callerAck );
        getLeg( callerAck.getSession() ).getCallService().answered();
    }

    protected void doCalleeRefer( SipServletRequest calleeRefer ) throws ServletException, IOException {
        // Agent has triggered a transfer!
        super.doCalleeRefer( calleeRefer );
        CallService call = getLeg( calleeRefer.getSession() ).getCallService();
        try {
            String aalId = getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() ).getActivityLogId();
            AgentActivityLogger aal = getServiceFactory().getLogService().getAgentActivityLogger( aalId );
            aal.addTransferService( call.getId(), calleeRefer.getAddressHeader( REFER_TO ).toString() );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }

    }

    protected void doCallee1xx( SipServletResponse callee1xx ) throws ServletException, IOException {
        if ( callee1xx.getStatus() == SipServletResponse.SC_RINGING ) {
            getCallService( callee1xx.getSession() ).ringing();
        } else if ( callee1xx.getStatus() == SipServletResponse.SC_SESSION_PROGRESS ) {
            getCallService( callee1xx.getSession() ).inGreeting();
        }
        super.doCallee1xx( callee1xx );
    }


    protected void doRedirectResponse( SipServletResponse response ) throws ServletException, IOException {
        if ( response.getStatus() == SipServletResponse.SC_MOVED_TEMPORARILY ) {
            Leg leg = getLeg( response.getSession() );
            CallService call = leg.getCallService();
            if ( leg instanceof UACLeg ) { // 302 from callee/agent

                // Log the call as "not answered"
                if ( !call.isAbandoned() ) {
                    try {
                        getServiceFactory().getAgentService().getAgentByAOR( call.getHandlerAgent() ).notAnswered();
                    } catch ( AgentNotAvailableException e ) {
                        logger.severe( e.getMessage() );
                    }
                }

                // Since call forwarding is not supported, we'll reject caller with 404 response code
                leg.getPeer().getInitialRequest().createResponse( SipServletResponse.SC_NOT_FOUND ).send();
                leg.getPeer().setState( LegState.IDLE );

                // Terminate callee
                leg.setState( LegState.IDLE );
            }
        } else {
            super.doRedirectResponse( response );
        }
    }


    protected void doCalleeBye( SipServletRequest calleeBye ) throws ServletException, IOException {
        try {
            getCallService( calleeBye.getSession() ).setState( CallService.CALLEE_TEARDOWN );
            if ( getServiceFactory().getPoolService().getPool(
                    getCallService( calleeBye.getSession() ).getHandlerQueueName()
            ).doesApplicationInvolve( getCallService( calleeBye.getSession() ) ) ) {
                getLeg( calleeBye.getSession() ).handleBye( calleeBye );
            } else {
                super.doCalleeBye( calleeBye );
            }
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            super.doCalleeBye( calleeBye );
        }
    }


}