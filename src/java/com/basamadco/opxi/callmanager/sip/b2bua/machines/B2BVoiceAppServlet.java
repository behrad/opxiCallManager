package com.basamadco.opxi.callmanager.sip.b2bua.machines;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.call.LegState;
import com.basamadco.opxi.callmanager.sip.b2bua.GenericB2BUA;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 21, 2008
 *         Time: 4:06:25 PM
 */
public class B2BVoiceAppServlet extends GenericB2BUA {

    private static final Logger logger = Logger.getLogger( B2BVoiceAppServlet.class.getName() );


    protected URI getCalleeURI( SipServletRequest request ) throws OpxiException, ServletException {
        throw new IllegalStateException( "Invalid method called." );
    }

    protected String getUACRoleName() {
        return com.basamadco.opxi.callmanager.call.Leg.VOICE_APP;
    }

    /*protected void doCallee1xx( SipServletResponse callee1xx ) throws ServletException, IOException {
        super.doCallee1xx( callee1xx );    //To change body of overridden methods use File | Settings | File Templates.
    }*/

    protected void doCallee200ToInvite( SipServletResponse callee200 ) throws ServletException, IOException {
        Leg callee = getLeg( callee200.getSession() );
        Leg caller = getPeerLeg( callee200.getSession() );
        if( caller.getState().equals( LegState.IN_CALL ) ) {
            try {
                SipServletRequest reInvite = caller.getSession().createRequest( INVITE );
                copyContent( callee200, reInvite );
                reInvite.send();
                caller.setState( LegState.REFRESH_INVITE_SENT );
                getCallService( caller.getSession() ).setState( CallService.TRANSFERING );
            } catch ( IllegalStateException e ) {
                logger.severe( "Could not reinvite caller: " + e );
                // BAD ACK!
                callee200.createAck().send(); // This forces a BYE from callee!!!
                // TODO send BYE? this makes a "dead sure" 481 and an forced Exception in our app for that.
            }
        } else {
            logger.warning( "B2BVoiceApp Machine got 200 from callee in caller state '" + caller.getState() + "'" );
            super.doCallee200ToInvite( callee200 );
        }
    }

    protected void doCaller200ToInvite( SipServletResponse caller200 ) throws ServletException, IOException {
        SipSession caller = caller200.getSession();
        CallService call = getCallService( caller );
        if( getLegState( caller ).equals( LegState.REFRESH_INVITE_SENT ) ) {
            // Caller answer to refresh invite
            // 1. Ack caller's answer
            Leg callerLeg = getLeg( caller );
            callerLeg.setState( LegState.IN_CALL );
            caller200.createAck().send();

            // 2. Ack callee with the caller's answer
            Leg callee = getPeerLeg( caller );
            callee.setState( LegState.IN_CALL );
            SipServletRequest calleeACK = callee.getSuccessResponse().createAck();
            copyContent( caller200, calleeACK );
            calleeACK.send();

            getCallService( caller ).setState( CallService.IN_CALL );
        } else {
            super.doCaller200ToInvite( caller200 );
        }
    }
}
