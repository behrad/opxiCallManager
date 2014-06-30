package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.SipService;

import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: Jul 16, 2006
 *         Time: 5:09:44 PM
 */
public class UASLeg extends Leg {

    private static final Logger logger = Logger.getLogger( UASLeg.class.getName() );

    public UASLeg( SipService sipFactory, CallService call, SipServletRequest req, String roleName ) {
        super( sipFactory, call, req, roleName );
    }

    public void reject( int status, String msg ) throws IOException {
        LegState lstate = getState();
        if ( lstate.equals( LegState.TRYING ) || lstate.equals( LegState.RINGING ) ) {
            try {
                if ( msg != null ) {
                    getInitialRequest().createResponse( status, msg ).send();
                } else {
                    getInitialRequest().createResponse( status ).send();
                }
            } catch ( IllegalStateException e ) {
                logger.warning( "App is not invoked by received CANCEL container!" );
            }
            setState( LegState.IDLE ); // SipServlet apps are not invoked for non-2xx ACKs!
        } else {
            throw new IllegalStateException( "Could not reject leg in state '" + lstate + "'" );
        }
    }

    public void terminate() throws IOException {
        if ( getState().equals( LegState.RINGING ) || getState().equals( LegState.TRYING ) ) {
            getSavedRequest().createResponse( SipServletResponse.SC_TEMPORARLY_UNAVAILABLE ).send();
            setState( LegState.IDLE );
        } else {
            super.terminate();
        }
    }

    @Override
    protected void legStateUpdated() {
        /*try {
            Presence cmAgentPresence = sipService.getServiceFactory().getPresenceService().getPresence(
                    new UserAgent( getInitialRequest().getFrom().getURI() )
            );

            if ( state.equals( LegState.TRYING ) ) {
                cmAgentPresence.setNote( "Call Setup" );
                cmAgentPresence.setActivity( Presence.ACTIVITY_ON_THE_PHONE );
            } else if ( state.equals( LegState.IN_CALL ) ) {
                cmAgentPresence.setNote( "TALKING" );
                cmAgentPresence.setActivity( Presence.ACTIVITY_ON_THE_PHONE );
            } else if ( state.equals( LegState.IDLE ) ) {
                cmAgentPresence.setNote( "IDLE" );
                cmAgentPresence.setActivity( Presence.ACTIVITY_UNKNOWN );
            }
            sipService.getServiceFactory().getPresenceService().notifyPresence( cmAgentPresence );
        } catch ( OpxiException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        }
*/
    }

    /*public void canceled() {
        try {
            Presence cmAgentPresence = sipService.getServiceFactory().getPresenceService().getPresence(
                    new UserAgent( getInitialRequest().getFrom().getURI() )
            );
            cmAgentPresence.setNote( "IDLE" );
            cmAgentPresence.setActivity( Presence.ACTIVITY_UNKNOWN );
            sipService.getServiceFactory().getPresenceService().notifyPresence( cmAgentPresence );
        } catch ( OpxiException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        }
    }*/
}
