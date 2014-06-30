package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.SipService;

import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 16, 2006
 *         Time: 5:09:21 PM
 */
public class UACLeg extends Leg {

    private final static Logger logger = Logger.getLogger( UACLeg.class.getName() );

    private boolean got1xx = false;

    public UACLeg( SipService sipFactory, CallService call, SipServletRequest req, String roleName ) {
        super( sipFactory, call, req, roleName );
    }

    public void cancel() throws IOException {
        LegState lState = getState();
        if ( lState.equals( LegState.TRYING ) ) {
            getInitialRequest().createCancel().send();
            setState( LegState.FAILURE );
//        } else if( lState.equals( LegState.IN_CALL ) ) { //RACE! 200 has won.
//            bye();
        } else {
            throw new IllegalStateException( "Could not issue CANCEL in state '" + lState + "'" );
        }
    }

    public boolean hasGot1xx() {
        return got1xx;
    }

    public void setGot1xx() {
        got1xx = true;
    }

    public void terminate() throws IOException {
        if ( getState().equals( LegState.TRYING ) ) {
            cancel();
        } else {
            super.terminate();    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    public void dispose() {
        if ( getRoleName().equalsIgnoreCase( AGENT ) ) {
            try {
                getCallService().releaseAgent();
            } catch ( OpxiException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
        }
        super.dispose();
    }
}
