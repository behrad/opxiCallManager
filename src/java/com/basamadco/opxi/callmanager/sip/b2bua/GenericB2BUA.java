package com.basamadco.opxi.callmanager.sip.b2bua;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.*;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: May 21, 2006
 *         Time: 12:11:33 PM
 */
public abstract class GenericB2BUA extends B2BUA {

    private static final Logger logger = Logger.getLogger( GenericB2BUA.class.getName() );

    private static final int TRANSFER_TIMER_DELAY =
            Integer.parseInt( PropertyUtil.getProperty( "opxi.callmanager.transfer.referInviteTimer.timeout" ) ) * 1000;


    protected void doCallerInvite( SipServletRequest callerInvite ) throws OpxiException, ServletException, IOException {
        SipSession callerSession = callerInvite.getSession();
        Leg callerLeg = getLeg( callerSession );
//        callerLeg.setState( LegState.TRYING );

        SipServletRequest peerInvite = callerLeg.createRequest( callerInvite, getCalleeURI( callerInvite ) );
        // this version of createRequest implicitly copies the caller session handler
        peerInvite.getSession().setHandler( getServletName() );

        callerLeg.createPeerLeg( peerInvite, getUACRoleName() );
        peerInvite.send();
    }

    protected void doUASReInvite( SipServletRequest callerReInvite ) throws ServletException, IOException {
        CallService call = getCallService( callerReInvite.getSession() );
        //if( front.isInRole( callerReInvite.getSession(), Leg.CALLER ) ) {
        if ( call.getState() == CallService.IN_CALL ) {
            refreshDialog( callerReInvite );
        } else {
            callerReInvite.createResponse( SipServletResponse.SC_SERVICE_UNAVAILABLE ).send();
        }
        //} else {
        //    refreshDialog( callerReInvite );
        //}
    }

    protected void doUACReInvite( SipServletRequest calleeReInvite ) throws ServletException, IOException {
        refreshDialog( calleeReInvite );
    }


    private void refreshDialog( SipServletRequest reInvite ) throws ServletException, IOException {
        Leg peer = getPeerLeg( reInvite.getSession() );
        if ( peer.getState().equals( LegState.IN_CALL ) ) {
            SipServletRequest reinvite2 = peer.createRequest( reInvite );
            reinvite2.send();
        } else {
            reInvite.createResponse( SipServletResponse.SC_SERVICE_UNAVAILABLE ).send();
        }
    }

    protected void doCallerAck( SipServletRequest callerAck ) throws ServletException, IOException {
        Leg callerLeg = getLeg( callerAck.getSession() );
        LegState state = callerLeg.getState();
        if ( state.equals( LegState.ACK_PENDING ) ) {
            callerLeg.setState( LegState.IN_CALL );
            Leg peer = getPeerLeg( callerAck.getSession() );
            if ( peer.getState().equals( LegState.ACK_PENDING ) ) {
                peer.setState( LegState.IN_CALL );
                peer.getSuccessResponse().createAck().send();
            }
        } else if ( state.equals( LegState.FAILURE ) ) {
            callerLeg.setState( com.basamadco.opxi.callmanager.call.LegState.IDLE );
            logger.finer( "What to do know?" );
        } else if ( state.equals( LegState.END ) ) {
            logger.finer( "It's to late for the ACK, dialog is END." );
            // Eat the ACK!
        } else if ( state.equals( LegState.IDLE ) ) {
            logger.finer( "It's to late for the ACK, dialog is IDLE." );
        }
//        else {
//            throw new IllegalStateException( "B2BUA couldn't handle ack in state: " + state );
//        }
    }

    protected void doCalleeAck( SipServletRequest calleeAck ) throws ServletException, IOException {
        LegState state = getLegState( calleeAck.getSession() );
        if ( state.equals( LegState.IDLE ) ) {
            logger.finer( "It's to late for the ACK, dialog is ended." );
            // Eat the ACK!
        } else {
            doCallerAck( calleeAck );
        }
    }

    protected void doCallerCancel( SipServletRequest callerCancel ) throws ServletException, IOException {
        SipSession caller = callerCancel.getSession();
        Leg callerleg = getLeg( caller );
        LegState callerState = callerleg.getState();
        try {
            UACLeg calleeLeg = (UACLeg) getPeerLeg( callerCancel.getSession() );
            LegState calleeState = calleeLeg.getState();
            callerleg.getCallService().setState( CallService.CANCELED );
            callerleg.getCallService().disconnected( callerleg );
            if ( callerState.equals( LegState.TRYING )
                    || callerState.equals( LegState.RINGING )
                    || callerState.equals( LegState.ACK_PENDING ) ) {
                if ( calleeState.equals( LegState.TRYING ) ) {
                    //      TODO to handle cancel races
//                    if( calleeLeg.hasGot1xx() ) {

                    calleeLeg.cancel();
//                    }
                    //      TODO to handle cancel races
                } else if ( calleeState.equals( LegState.ACK_PENDING )
                        || calleeState.equals( LegState.IN_CALL ) ) { // hasGot2xx
                    calleeLeg.terminate();
                } else if ( !calleeState.equals( LegState.IDLE ) ) {
                    throw new IllegalStateException( "B2BUA couldn't handle cancel in callee state: " + calleeState );
                }
            } else if ( callerState.equals( LegState.FAILURE ) ) {
                logger.finer( "B2BUA got a CANCEL in caller FAILURE mode, callee state is: " + calleeState );
            } else {
                throw new IllegalStateException( "B2BUA couldn't handle cancel in state: " + callerState );
            }
        } finally {
            // We're forcing front leg termination on CANCEL
            callerleg.setState( LegState.IDLE );
        }
    }

    protected void doCallerBye( SipServletRequest callerBye ) throws ServletException, IOException {
        doByeAction( callerBye );
    }

    protected void doCalleeBye( SipServletRequest calleeBye ) throws ServletException, IOException {
        doByeAction( calleeBye );
    }

    protected void doCalleeRefer( SipServletRequest calleeRefer ) throws ServletException, IOException {
//        if( getLeg( calleeRefer.getSession() ).getRoleName().equals( Leg.AGENT ) )
        Leg caller = getPeerLeg( calleeRefer.getSession() );
        SipServletRequest refer = caller.createRefer( calleeRefer );
        refer.send();
    }

    protected void doReferAccepted( SipServletResponse refer202 ) throws ServletException, IOException {
        CallService transferee = getLeg( refer202.getSession() ).getCallService();
        transferee.createTransferTimer( TRANSFER_TIMER_DELAY ); // This timer handles transfer INVITE misses
        forwardResponse( refer202 );
    }

    protected void doCaller200ToInvite( SipServletResponse caller200 ) throws ServletException, IOException {
        do200Action( caller200 );
    }

    protected void do200Action( SipServletResponse a200 ) throws ServletException, IOException {
        Leg leg = getLeg( a200.getSession() );
        LegState calleeState = leg.getState();
        if ( calleeState.equals( LegState.TRYING ) || calleeState.equals( LegState.IN_CALL ) ) {
//            leg.setSuccessResponse( a200 );
            leg.setState( LegState.ACK_PENDING ); // IN_CALL before!
        } else if ( calleeState.equals( LegState.FAILURE ) ) { // RACE: callee is being canceled by caller
            a200.createAck().send();
            leg.terminate();
            return;
        } else if ( calleeState.equals( LegState.IDLE ) ) { // caller has sent BYE immediately after 200
            a200.createAck().send();
            return;
        } else {
            if ( !calleeState.equals( LegState.END ) ) { // TODO remove if 1
                if ( !calleeState.equals( LegState.ACK_PENDING ) ) { // TODO remove if 2
                    throw new IllegalStateException( "B2BUA couldn't handle 200/INVITE for state: " + calleeState );
                }
            }
        }
        Leg callerLeg = getPeerLeg( a200.getSession() );
        LegState callerState = callerLeg.getState();
        if ( callerState.equals( LegState.TRYING ) || callerState.equals( LegState.RINGING ) ||
                callerState.equals( LegState.IN_CALL ) ) {
            callerLeg.setState( LegState.ACK_PENDING );
            forwardResponse( a200 ); // Send 200
        } else if ( !callerState.equals( LegState.IDLE ) ) { // 1 ?
            if ( !callerState.equals( LegState.IN_CALL ) ) { // TODO remove if 2
                throw new IllegalStateException( "B2BUA couldn't handle 200/INVITE for state: " + callerState );
            }
        }
    }

    protected void doCallee200ToInvite( SipServletResponse callee200 ) throws ServletException, IOException {
        do200Action( callee200 );
    }

    protected void doCallee200ToCancel( SipServletResponse callee200 ) throws ServletException, IOException {
        getLeg( callee200.getSession() ).setState( com.basamadco.opxi.callmanager.call.LegState.IDLE );
    }

    protected void doCaller200ToBye( SipServletResponse caller200 ) throws ServletException, IOException {
        Leg callerLeg = getLeg( caller200.getSession() );
        LegState state = callerLeg.getState();
        if ( state.equals( LegState.END ) ) {
            callerLeg.setState( LegState.IDLE );
        } else if ( !state.equals( LegState.IDLE ) ) {
            throw new IllegalStateException( "B2BUA couldn't handle 200/BYE for state: " + state );
        }
    }

    protected void doCallee200ToBye( SipServletResponse callee200 ) throws ServletException, IOException {
        doCaller200ToBye( callee200 );
    }

    protected void doCalleeErrorToInvite( SipServletResponse calleeError ) throws ServletException, IOException {
        Leg callee = getLeg( calleeError.getSession() );
        LegState calleeState = callee.getState();
        if ( callee instanceof UACLeg && calleeError.getStatus() == 481 ) { // treat callee 481s as a special response
            // but ignore caller 481s since they are errors to in-dialog reInvites
            callee.setState( LegState.IDLE );
            // ignoring peer leg may not be so harmful!
            return;
        }
        if ( calleeState.equals( LegState.TRYING ) ) {
            Leg callerLeg = getPeerLeg( calleeError.getSession() );
            LegState callerState = callerLeg.getState();
            if ( callerState.equals( LegState.RINGING ) || callerState.equals( LegState.TRYING ) ) {
                // Send >2xx
                forwardResponse( calleeError );
                callerLeg.setState( LegState.IDLE );
            }
            if ( callerState.equals( LegState.IN_CALL ) ) {
                logger.warning( "Added new B2BUA state handling..." );
                callerLeg.terminate();
            } else {
                logger.finer( "B2BUA couldn't handle " + calleeError.getStatus() + "/Invite in state " + callerState );
//                forwardResponse( calleeError ); // TODO why?
            }
            callee.setState( LegState.IDLE );
//            if( calleeError.getStatus() == SipServletResponse.SC_REQUEST_TIMEOUT ) {
//                if( calleeError.getRequest().isCommitted() ) {
//                    calleeError.getRequest().createCancel().send();
//                    getInitialRequest( getPeerLeg( calleeError ) ).createResponse( SipServletResponse.SC_REQUEST_TIMEOUT ).send();
//                }
//            }
        } else if ( calleeState.equals( LegState.IN_CALL ) ) { // Re-Invite error handling
            Leg callerLeg = getPeerLeg( calleeError.getSession() );
            LegState callerState = callerLeg.getState();
            if ( callerState.equals( LegState.IN_CALL ) ) {
                forwardResponse( calleeError );
            }
        } else if ( calleeState.equals( LegState.FAILURE ) ) {
            callee.setState( LegState.IDLE );
            if ( calleeError.getStatus() == SipServletResponse.SC_REQUEST_TERMINATED ) {
                logger.finer( "Receive 487 for canceled Invite." );
//                callee.setState( LegState.IDLE );
                logger.finer( "******************************** 1" );
            } else if ( calleeError.getStatus() == SipServletResponse.SC_REQUEST_TIMEOUT ) {
                // container generated 408, callee is canceled, so ignore this!
//                Leg callerLeg = getPeerLeg( calleeError.getSession() );
//                LegState callerState = callerLeg.getState();
//                if( !callerState.equals( LegState.IDLE ) ) {
//                    throw new IllegalStateException( "B2BUA got a 408 and caller is in state: " + callerState );
//                }
                logger.finer( "Caller leg is already disposed! Should I pay attention to caller?" );
//                callee.setState( LegState.IDLE );
                return;
            } else {
                logger.severe( "B2BUA couldn't handle " + calleeError.getStatus() + "/Invite" );
            }
        } else if ( calleeState.equals( LegState.IDLE ) ) {
            logger.finer( "B2BUA receive " + calleeError.getStatus() + "/Invite in state IDLE." );
        } else {
            throw new IllegalStateException( "B2BUA couldn't handle " + calleeError.getStatus() + "/Invite for state: " + calleeState );
        }
    }

    protected void doCallerErrorToInvite( SipServletResponse callerError ) throws ServletException, IOException {
        doCalleeErrorToInvite( callerError );
    }

    protected void doCallee1xx( SipServletResponse callee1xx ) throws ServletException, IOException {


        //TODO to handle cancel races
//        ((UACLeg)getLeg( callee1xx.getSession() ) ).setGot1xx();
//        if( getLegState( callee1xx.getSession() ).equals( LegState.FAILURE ) ) { // A cancel has been marked
//            callee1xx.getRequest().createCancel().send();
//        }
        //TODO to handle cancel races
        Leg callee = getLeg( callee1xx.getSession() );
        if ( !callee.getState().equals( LegState.FAILURE ) ) { // check if callee is not already canceled!
            Leg caller = getPeerLeg( callee1xx.getSession() );
            LegState callerState = caller.getState();
            if ( callerState.equals( LegState.TRYING ) || callerState.equals( LegState.RINGING ) ) {
                caller.setState( LegState.RINGING );
                forwardResponse( callee1xx );
            }
        }
    }

    protected void doErrorToRefer( SipServletResponse response ) throws IOException, ServletException {
        Leg leg = getLeg( response.getSession() );
        if ( response.getStatus() == SipServletResponse.SC_REQUEST_TIMEOUT ||
                response.getStatus() == 481 ) {
            if ( leg.getCallService().getState() == CallService.ASSIGNED ) {
                if ( leg instanceof UASLeg ) {
                    // caller (in every state or position he/she is) is gone!
                    leg.getCallService().teardown( "Caller no response to transfer event" );
                }
            }
        }
    }

    protected void doErrorToBye( SipServletResponse response ) throws IOException, ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void doErrorToCancel( SipServletResponse response ) throws IOException, ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
