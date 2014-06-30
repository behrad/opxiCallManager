package com.basamadco.opxi.callmanager.sip.b2bua;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.*;
import com.basamadco.opxi.callmanager.sip.CallServlet;
import com.basamadco.opxi.callmanager.sip.NoLegAttributeIsSetException;
import com.basamadco.opxi.callmanager.util.LockManager;

import javax.servlet.ServletException;
import javax.servlet.sip.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A back 2 back user agent
 *
 * @author Jrad
 */
public abstract class B2BUA extends CallServlet {

    private static final Logger logger = Logger.getLogger( B2BUA.class.getName() );

//    protected static final String CALLER = "Caller"; // UAS role

//    protected static final String CALLEE = "Callee"; // UAC role

//    private static final String INITIAL_REQUEST = "initial-request";

//    private static final String B2BUA_LEG_STATE = "B2BUA-Leg-LegState";

//    private static final String B2BUA_LEG_ROLE = "B2BUA-Leg-Role";

//    private static final String PEER_REQ = "peer.req";

//    private static final String PEER = "peer";

//    private static final String RESP_INV = "response2Invite";

//    private static final Object UNI_LEG_PLACE_HOLDER = new Object();

    protected void doRequest( SipServletRequest request ) throws ServletException, IOException {
        try {
            Leg leg = getLeg( request.getSession() );
            if ( leg.getState() == LegState.IDLE ) {
                logger.warning( "Cannot handle the request on an IDLE leg: " + leg );
                if ( !request.isCommitted() ) {
                    request.createResponse( 481, "Bound Leg IDLE" ).send();
                }
            } else {
                super.doRequest( request );
            }
        } catch ( IllegalStateException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        }
    }

    protected void doResponse( SipServletResponse response ) throws ServletException, IOException {
        try {
            Leg leg = getLeg( response.getSession() );
            if ( leg.getState() == LegState.IDLE ) {
                logger.warning( "This response is ignored since received on an IDLE leg." );
            } else {
                super.doResponse( response );
            }
        } catch ( IllegalStateException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        }
    }

    protected void doInitialInvite( SipServletRequest invite ) throws OpxiException, ServletException, IOException {
        logger.finer( leg( invite ) + " receive initial INVITE." );
        Leg caller = getLeg( invite.getSession() );
        try {
            invite.getSession().setHandler( getServletName() );
            doCallerInvite( invite );
        } catch ( IllegalStateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            caller.setState( LegState.IDLE );
            throw e;
        }
//        logCallState( invite.getSession() );
    }

    protected void doReInvite( SipServletRequest re_invite1 ) throws ServletException, IOException {
//        logCallState( re_invite1.getSession() );
        logger.finer( leg( re_invite1 ) + " receive reINVITE." );
        Leg leg = getLeg( re_invite1.getSession() );
        leg.setSavedRequest( re_invite1 );
        try {
            if ( getLeg( re_invite1.getSession() ) instanceof UASLeg ) {
                doUASReInvite( re_invite1 );
            } else {
                doUACReInvite( re_invite1 );
            }
        } catch ( IllegalStateException e ) {
            leg.setState( LegState.IDLE );
            throw e;
        }
//        logCallState( re_invite1.getSession() );
    }

    protected void doAck( SipServletRequest ack1 ) throws ServletException, IOException {
        logger.finer( leg( ack1 ) + " receive ACK." );
//        logCallState( ack1.getSession() );
        Leg leg = getLeg( ack1.getSession() );
        try {
            if ( leg instanceof UASLeg ) {
                doCallerAck( ack1 );
            } else {
                doCalleeAck( ack1 );
            }
            super.doAck( ack1 );
        } catch ( IllegalStateException e ) {
            leg.setState( LegState.IDLE );
            throw e;
        }
//        logCallState( ack1.getSession() );
    }

    protected void doCancel( SipServletRequest cancel ) throws ServletException, IOException {
        logger.finer( leg( cancel ) + " Receive CANCEL." );
//        logCallState( cancel.getSession() );
        Leg leg = getLeg( cancel.getSession() );
        try {
            if ( leg instanceof UASLeg && !leg.getPeer().equals( UniLegPlaceHolder.INSTANCE ) ) {
                synchronized ( LockManager.getCancel_200_RaceLock( getCallService( cancel.getSession() ) ) ) {
                    doCallerCancel( cancel );
                }
            } else {
                leg.setState( LegState.IDLE );
//                throw new IllegalStateException( "Callee should not receive a CANCEL request." );
            }
            super.doCancel( cancel );
        } catch ( IllegalStateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            if ( !leg.getState().equals( LegState.IDLE ) ) {
                leg.setState( LegState.IDLE );
            }
            throw e;
        }
//        logCallState( cancel.getSession() );
    }

    protected void doBye( SipServletRequest bye ) throws ServletException, IOException {
        logger.finer( leg( bye ) + " Receive BYE." );
        Leg leg = getLeg( bye.getSession() );
        leg.getCallService().disconnected( leg );
        try {
            if ( leg instanceof UASLeg ) {
                doCallerBye( bye );
            } else {
                doCalleeBye( bye );
            }
            super.doBye( bye );
        } catch ( IllegalStateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            if ( !leg.getState().equals( LegState.IDLE ) ) {
                leg.setState( LegState.IDLE );
            }
            throw e;
        }
//        logCallState( bye.getSession() );
    }

    protected void doRefer( SipServletRequest refer ) throws ServletException, IOException {
        logger.finer( leg( refer ) + " Receive REFER." );
        Leg leg = getLeg( refer.getSession() );
        leg.setSavedRequest( refer );
        if ( leg instanceof UASLeg ) {
            refer.createResponse( SipServletResponse.SC_SERVICE_UNAVAILABLE, "Caller Transfer Request Not Supported" ).send();
        } else {
            Leg caller = getPeerLeg( refer.getSession() );
            if ( caller.getState().equals( LegState.IN_CALL ) ) {
                doCalleeRefer( refer );
            } else {
                refer.createResponse( SipServletResponse.SC_DECLINE, "Caller Not Stable" ).send();
            }
        }
    }

    protected void doNotify( SipServletRequest notify ) throws ServletException, IOException {
        logger.finer( leg( notify ) + " Receive NOTIFY." );
        Leg leg = getLeg( notify.getSession() );
        Leg peer = getPeerLeg( notify.getSession() );
        if ( peer == null || peer instanceof UniLegPlaceHolder ) {
            notify.createResponse( SipServletResponse.SC_OK ).send();
        } else {
            leg.setSavedRequest( notify );
            peer.createNotify( notify ).send();
        }
    }

    protected void doSubscribe( SipServletRequest subscribe ) throws ServletException, IOException {
        logger.finer( leg( subscribe ) + " Receive SUBSCRIBE." );
        Leg leg = getLeg( subscribe.getSession() );
        if ( leg.getState() != LegState.IDLE ) {
            leg.setSavedRequest( subscribe );
            Leg peer = getPeerLeg( subscribe.getSession() );
            peer.createRequest( subscribe ).send();
        } else {
            logger.warning( "Receive SUBSCRIBE on ando IDLE leg... generate 200/OK." );
            subscribe.createResponse( SipServletResponse.SC_OK ).send();
        }
    }

    protected void doSuccessResponse( SipServletResponse response ) throws ServletException, IOException {
        logger.finer( leg( response ) + " Receive 2xx response: " + response.getStatus() + "/" + response.getMethod() );
        Leg myLeg = getLeg( response.getSession() );
        try {
//        logCallState( response.getSession() );
            if ( response.getMethod().equals( REFER ) ) {
                doReferAccepted( response );
            } else if ( response.getMethod().equals( INVITE ) ) {
                myLeg.setSuccessResponse( response );
                if ( myLeg instanceof UASLeg ) {
                    doCaller200ToInvite( response );
                } else {
                    synchronized ( LockManager.getCancel_200_RaceLock( getCallService( response.getSession() ) ) ) {
                        doCallee200ToInvite( response );
                    }
                }
//                    myLeg.setState( LegState.ACK_PENDING );
            } else if ( response.getMethod().equals( BYE ) ) {
                if ( getLeg( response.getSession() ) instanceof UASLeg ) {
                    doCaller200ToBye( response );
                } else {
                    doCallee200ToBye( response );
                }
            } else if ( response.getMethod().equals( CANCEL ) ) {
                if ( getLeg( response.getSession() ) instanceof UACLeg ) {
                    doCallee200ToCancel( response );
                } else {
                    throw new IllegalStateException( "Caller should not receive a 200 to cancel" );
                }
            } else {
                logger.warning( "B2BUA has not recognized correct decision! Forwarding Response." );
                forwardResponse( response );
            }
            //        logCallState( response.getSession() );
            super.doSuccessResponse( response );
        } catch ( IllegalStateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
//            myLeg.setState( LegState.IDLE );
        }
    }


    protected void doRedirectResponse( SipServletResponse response ) throws ServletException, IOException {
        logger.finer( leg( response ) + " Receive 3xx response: " + response.getStatus() + "/" + response.getMethod() );
        forwardResponse( response );
    }

    protected void doErrorResponse( SipServletResponse errorResponse ) throws ServletException, IOException {
//        log( errorResponse.toString() );
        logger.finer( leg( errorResponse ) +
                " Receive error response: " + errorResponse.getStatus() + "/" + errorResponse.getMethod() );
//        logCallState( errorResponse.getSession() );
        Leg leg = getLeg( errorResponse.getSession() );
        try {
            if ( errorResponse.getMethod().equals( INVITE ) ) {
                if ( leg instanceof UACLeg ) {
                    doCalleeErrorToInvite( errorResponse );
                    //                if( !getLegState( errorResponse.getSession() ).equals( LegState.IN_CALL ) ) { // !isInitial()
                    //                    // TODO removed for handling 478 to CANCELed terminations!
                    ////                    if( errorResponse.getStatus() != SipServletResponse.SC_REQUEST_TERMINATED ) {
                    //                        CallService front = getCallService( errorResponse.getSession() );
                    //
                    //                    // CALLTERMINATE old-style front termination
                    ////                        if( front.getState() != CallService.DISPOSING ) {
                    ////                            String reason = getDisconnectReason( errorResponse );
                    //////                            logger.finer( "Handle Error Reponse: " + front + " for " +
                    //////                                    errorResponse.getStatus()+"/"+errorResponse.getMethod() );
                    ////                            terminate( front, reason );
                    ////                        }
                    //                    // CALLTERMINATE old-style front termination
                    //
                    ////                    }
                    //                }
                } else {
                    doCallerErrorToInvite( errorResponse );
                }
            } else if ( errorResponse.getMethod().equals( BYE ) ) {
                doErrorToBye( errorResponse );
                leg.setState( LegState.IDLE );
            } else if ( errorResponse.getMethod().equals( REFER ) ) {
                // Handle error to refer front transfer.
                doErrorToRefer( errorResponse );
            } else if ( errorResponse.getMethod().equals( CANCEL ) ) {
                doErrorToCancel( errorResponse );
            } else if ( leg.getState().equals( LegState.IDLE ) ) { // for other methods
                logger.warning( "Leg's state is IDLE, Simply try to relay response." );
                forwardResponse( errorResponse );
            } else {
                logger.warning( "B2BUA could not recognize the correct decision! Simply try to relay response." );
                forwardResponse( errorResponse );
            }
            //        logCallState( errorResponse.getSession() );
            super.doErrorResponse( errorResponse );
        } catch ( IllegalStateException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            leg.setState( LegState.IDLE );
            throw e;
        }
    }

    protected void doProvisionalResponse( SipServletResponse resp ) throws ServletException, IOException {
        logger.finer( leg( resp ) + " Receive 1xx response: " + resp.getStatus() + "/" + resp.getMethod() );
        Leg leg = getLeg( resp.getSession() );
        try {
            if ( leg instanceof UACLeg ) {
                doCallee1xx( resp );
            } else {
                throw new IllegalStateException( "Caller should not receive 1xx to its own response" );
            }
            super.doProvisionalResponse( resp );
        } catch ( IllegalStateException e ) {
            leg.setState( LegState.IDLE );
            throw e;
        }
    }

    protected abstract void doCallerInvite( SipServletRequest callerInvite ) throws OpxiException, ServletException, IOException;

    protected abstract void doUASReInvite( SipServletRequest callerReInvite ) throws ServletException, IOException;

    protected abstract void doUACReInvite( SipServletRequest calleeReInvite ) throws ServletException, IOException;

    protected abstract void doCallerAck( SipServletRequest callerAck ) throws ServletException, IOException;

    protected abstract void doCalleeAck( SipServletRequest calleeAck ) throws ServletException, IOException;

    protected abstract void doCallerCancel( SipServletRequest callerCancel ) throws ServletException, IOException;

    protected abstract void doCallerBye( SipServletRequest callerBye ) throws ServletException, IOException;

    protected abstract void doCalleeBye( SipServletRequest calleeBye ) throws ServletException, IOException;

    protected abstract void doCalleeRefer( SipServletRequest calleeRefer ) throws ServletException, IOException;

    protected abstract void doReferAccepted( SipServletResponse refer202 ) throws ServletException, IOException;

    protected abstract void doCaller200ToInvite( SipServletResponse caller200 ) throws ServletException, IOException;

    protected abstract void doCallee200ToInvite( SipServletResponse callee200 ) throws ServletException, IOException;

    protected abstract void doCallee200ToCancel( SipServletResponse callee200 ) throws ServletException, IOException;

    protected abstract void doCaller200ToBye( SipServletResponse caller200 ) throws ServletException, IOException;

    protected abstract void doCallee200ToBye( SipServletResponse callee200 ) throws ServletException, IOException;

    protected abstract void doCallerErrorToInvite( SipServletResponse callerError ) throws ServletException, IOException;

    protected abstract void doCalleeErrorToInvite( SipServletResponse calleeError ) throws ServletException, IOException;

    protected abstract void doCallee1xx( SipServletResponse callee1xx ) throws ServletException, IOException;

    protected abstract URI getCalleeURI( SipServletRequest request ) throws OpxiException, ServletException;

    protected abstract String getUACRoleName();

    protected abstract void doErrorToRefer( SipServletResponse response ) throws IOException, ServletException;

    protected abstract void doErrorToBye( SipServletResponse response ) throws IOException, ServletException;

    protected abstract void doErrorToCancel( SipServletResponse response ) throws IOException, ServletException;

    /*
    * <__----========== B2BUA Utility Methods ==========----__>
    */

//    protected CallService getCallService( SipServletRequest request ) throws ServletException {
////        try {
//            if( super.getCallService( request ) == null )
//                return super.getCallService( getPeerLeg( request ) );
//            return super.getCallService( request );
////        } catch ( ServletException e ) {
////            logger.log( Level.SEVERE, e.getMessage(), e );
////            throw new OpxiException( e.getMessage(), e );
////        }
//    }

    protected void doByeAction( SipServletRequest bye ) throws ServletException, IOException {
        SipSession byer = bye.getSession();
//         byee;
        try {
            // TODO is this if unnecessary?
            if ( getLeg( byer ).getState().equals( LegState.IN_CALL ) ) {
                logger.finer( "Trying to send BYE to peer leg..." );
                Leg byee = getPeerLeg( byer );
                // 2. Send BYE request to second party and ready for their 200/OK
                byee.terminate();
            }
        } catch ( IllegalStateException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        } finally {
            // 1. Accept first party BYE with a 200/OK
            getLeg( byer ).handleBye( bye );
        }
    }

//    protected SipServletRequest createB2BRequest( SipServletRequest request, String roleName ) throws OpxiException, ServletException {
//        return createB2BRequest( request, null, roleName );
//    }
//
//    protected SipServletRequest createB2BRequest( SipServletRequest request, String RURI, String roleName )
//            throws OpxiException, ServletException {
//        SipServletRequest peer;
//        if( request.isInitial() ) {
//            RURI = ( RURI == null ) ? getCalleeURI( request ) : RURI;
//            peer = getLeg( request.getSession() ).createRequest( request, RURI );
//
//            setPeerSession( peer.getSession(), request.getSession() );
//            peer.setState( LegState.IDLE );
//            logger.finer( "Setting leg["+peer.getCallId()+"] handler: " + getServletName() );
//            peer.getSession().setHandler( getServletName() );
//            logger.finer(
//                    "Binding caller leg["+request.getCallId()+"] to new callee leg["+peer.getCallId()+"]"
//            );
//
////            request.setHeader( "Call-Info", "<http://192.168.128.30/opxiCallManager/test.jsp>;purpose=info" );
//        } else {
//            Leg leg2 = getPeerLeg( request.getSession() );
//            peer = leg2.getSession().createRequest( request.getMethod() );
//            copyContent( request, peer );
//        }
////        logger.finer( "Caller LegState: " + getLegState( request ) );
////        setLegState( peer.getSession(), getLegState( request.getSession() ) );
//        setPeerRequest( peer, request );
//        return peer;
//    }

    protected void forwardResponse( SipServletResponse resp1 ) throws ServletException, IOException {
        SipServletRequest peerReq = getPeerLeg( resp1.getSession() ).getSavedRequest();
        if ( peerReq != null ) {
            if ( !peerReq.isCommitted() ) { // Is this neccessary ?
                SipServletResponse resp2 = peerReq.createResponse( resp1.getStatus(), resp1.getReasonPhrase() );
                copyContent( resp1, resp2 );
                resp2.send();
            }
        } else {
            logger.warning( "No saved request on peer leg..." );
        }
    }

//    protected void setInitialRequest( SipServletRequest request ) {
//        request.getSession().setAttribute( INITIAL_REQUEST, request );
//    }
//
//    protected SipServletRequest getInitialRequest( SipSession session ) {
//        return (SipServletRequest)session.getAttribute( INITIAL_REQUEST );
//    }

//    protected void setLegState( SipSession leg, LegState state ) throws ServletException {
//        logger.finer( "Set leg state["+leg.getCallId()+"]: " + state );
////        leg.setAttribute( B2BUA_LEG_STATE, state );
//        getCallService( leg ).getLeg( leg ).setState( state );
//    }

    protected LegState getLegState( SipSession session ) throws ServletException {
        return getLeg( session ).getState();
//        return getCallService( leg ).getLeg( leg ).getState();
//        return (LegState)leg.getAttribute( B2BUA_LEG_STATE );
    }

//    protected void setRole( SipSession leg, String callLeg ) {
//        leg.setAttribute( B2BUA_LEG_ROLE, callLeg );
//    }

//    protected boolean isInCallerRole( SipServletMessage msg ) {
//        return msg.getSession().getAttribute( B2BUA_LEG_ROLE ).equals( CALLER );
//    }

//    protected void setPeerRequest( SipServletRequest peer_request, SipServletRequest original_request ) {
//        peer_request.setAttribute( PEER_REQ, original_request );
////        original_request.setAttribute( "peer.req", peer_request );
//    }

//    protected SipServletRequest getPeerRequest( SipServletRequest peer_request ) throws ServletException {
//        if ( peer_request.getAttribute( PEER_REQ ) == null )
//            throw new ServletException("no peer request set for this request: " + peer_request.getCallId() );
//        return (SipServletRequest)peer_request.getAttribute( PEER_REQ );
//    }

//    protected void setPeerSession( SipSession leg1, SipSession leg2 ) {
//        clearPeerSession( leg1 );
//        clearPeerSession( leg2 );
//        leg1.setAttribute( PEER, leg2 );
//        leg2.setAttribute( PEER, leg1 );
//        logger.finer( "Binding SipSessions: '" + leg1.getCallId() + "', '" + leg2.getCallId() + "'" );
//        if( getCallService( leg1 ) != null ) {
//            setSessionCall( leg2, getCallService( leg1 ) );
//            return;
//        } if( getCallService( leg2 ) != null ) {
//            setSessionCall( leg1, getCallService( leg2 ) );
//            return;
//        }
//    }

//    private void clearPeerSession( SipSession session ) {
//        try {
//            SipSession peerLeg = (SipSession)session.getAttribute( PEER );
//            if( peerLeg != null ) {
//                try {
//                    peerLeg.setAttribute( PEER, com.basamadco.opxi.callmanager.call.UniLegPlaceHolder.INSTANCE );
//                } catch( IllegalStateException e ) {
//                    logger.warning( "Could'nt remove peer session from '" + peerLeg.getCallId() + "': " + e.getMessage() );
//                }
//            }
//        } catch ( IllegalStateException e ) {
//            logger.severe( "SipSession '" + session.getCallId() + "' is already invalidated: " + e.getMessage() );
//            throw e;
//        }
//    }

    protected Leg getPeerLeg( SipSession session ) {
        return getLeg( session ).getPeer();
    }

//    protected SipSession getPeerSession( SipSession session ) {
//        SipSession peer = null;
//        try {
//            peer = (SipSession)session.getAttribute( PEER );
//        } catch( IllegalStateException e ) {
//            logger.severe( "SipSession '" + session.getCallId() + "' is already invalidated: " + e.getMessage() );
//            throw e;
//        }
//        if( peer == null ) {
//            throw new IllegalStateException( "No peer session set in session: '" + session.getCallId() + "'" );
//        }
//        return peer;
//    }

//    protected SipSession getPeerLeg( SipSession leg ) {
////    	logger.finer( "Retrieving peer leg for: " + msg.getId() );
////        logger.finer( "&&&&&&&&&&&&&&&&& start" );
//        SipSession leg2 = (SipSession)leg.getAttribute( PEER );
////        logger.finer( "PeerLeg["+leg.getId()+"]=" + leg2.getId() );
//        if( leg2 == null ) {
////            logger.finer( "&&&&&&&&&&&&&&&&& excep" );
//            throw new IllegalStateException("No peer dialog set for session: " + leg.getCallId() );
//        }
////        logger.finer( "&&&&&&&&&&&&&&&&& ohoh" );
//        return leg2;
//    }

//    protected SipSession getPeerLeg( SipServletMessage msg ) throws ServletException {
//        return getPeerLeg( msg.getSession() );
//    }

    /**
     * 1. Sets the sip session as a unary leg for B2BUA front manager,
     * and removes previously set "peer" leg if exists any.
     * 2. Sets the session as "Caller" so the B2BUA don't fall in session management.
     */
//    protected void setUniLeg( SipSession leg, String role ) {
//        leg.setAttribute( PEER, UNI_LEG_PLACE_HOLDER );
////        setRole( leg, role );
//    }
//
//    protected boolean isUniLeg( SipSession leg ) {
//        return leg.getAttribute( PEER ) == UNI_LEG_PLACE_HOLDER;
//    }

//    protected SipServletResponse getResponseToAck( SipServletMessage msg ) throws ServletException {
//        Leg leg2 = getPeerLeg( msg.getSession() );
//        letResponse resp2 = (SipServletResponse)leg2.getAttribute( RESP_INV );
//        leg2.removeAttribute( RESP_INV );
//        if( resp2 == null ) {
//            throw new IllegalStateException( "No response to ack set in session " + leg2.getCallId() );
//        }
//        return resp2;
//    }

//    protected void setResponseToAck( SipServletResponse resp ) {
//        resp.getSession().setAttribute( RESP_INV, resp );
//    }
//
//    protected SipServletResponse getResponseToAck( SipSession leg ) throws ServletException {
//        SipServletResponse resp = (SipServletResponse)leg.getAttribute( RESP_INV );
//        leg.removeAttribute( RESP_INV );
//        if( resp == null ) {
//            throw new IllegalStateException( "No response to ack set in session " + leg.getCallId() );
//        }
//        return resp;
//    }

//    protected void setB2BUAParam( SipServletRequest reinvite ) {
//        ( (SipURI)reinvite.getRequestURI() ).setParameter( OUTBOUND_B2BUA_LEG, "true" );
//    }
//
//    protected void setDisconnectReason( SipServletMessage msg, String reason ) {
//        msg.getSession().setAttribute( "disconnect-reason", reason );
//    }
//
//    protected String getDisconnectReason( SipServletMessage msg ) {
//        String reason = (String)msg.getSession().getAttribute( "disconnect-reason" );
//        if( reason == null ) {
//            if( msg instanceof SipServletRequest ) {
//                if( ( (SipServletRequest)msg ).getMethod().equals( BYE ) ) {
//                    reason = getLeg( msg.getSession() ) instanceof UACLeg ? "Call Manager Bye" : "Customer Bye";
//                }
//            } else {
//                SipServletResponse resp = (SipServletResponse)msg;
//                reason = resp.getStatus() + "-" + resp.getReasonPhrase();
//            }
//        }
//        return reason;
//    }

    // TODO this method is used only for logging purposes, remove method.
    protected String leg( SipServletMessage msg ) throws ServletException {
        try {
            return getLeg( msg.getSession() ).toString();
//        return getCallService( msg.getSession() ).getLeg( msg.getSession() ).toString();
        }
        catch ( NoLegAttributeIsSetException ex ) {
            return "Leg not exist.[Call-ID = '" + msg.getCallId() + "']";
        }
    }

}