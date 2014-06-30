package com.basamadco.opxi.callmanager.sip;

import com.basamadco.opxi.callmanager.call.*;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;

import javax.servlet.sip.URI;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SIP implementation of the CallController interface.
 * Establishes the connection, and uses B2BUA servlets as SIP b2bua machines
 * to continue handling call's SIP signaling.
 *
 * @author Jrad
 *         Date: Nov 23, 2006
 *         Time: 10:07:55 AM
 * @see com.basamadco.opxi.callmanager.call.CallController
 */
public class SipCallController implements CallController {

    private static final Logger logger = Logger.getLogger( SipCallController.class.getName() );


    public static final String GREETING_MACHINE = ApplicationConstants.GREETING_SERVLET;

    public static final String POST_GREETING_MACHINE = ApplicationConstants.POST_GREETING_SERVLET;

    public static final String WAIT_MACHINE = ApplicationConstants.QUEUED_SERVLET;

    public static final String WAITING_MSG_MACHINE = ApplicationConstants.WAITING_MSG_MACHINE;

    public static final String APPLICATION_MACHINE = ApplicationConstants.MEDIA_SERVLET;

    public static final String B2B_APPLICATION_MACHINE = ApplicationConstants.B2B_VOICEAPP_SERVLET;

    public static final String RINGING_APPLICATION_MACHINE = ApplicationConstants.RINGING_APPLICATION_MACHINE;


    private CallService callService;

    private String machineType;

    private String calleeRoleName;

    private URI calleeURI;

    private Address calleeAddress;

    private Address callerAddress;

    private RequestModifier requestModifier;

    private UASLeg UASLeg;


    public SipCallController( CallService callService ) {
        this.callService = callService;
    }


    public CallService getCallService() {
        return callService;
    }

    public void setCallService( CallService callService ) {
        this.callService = callService;
    }

    public URI getCalleeURI() {
        return calleeURI;
    }

    public void setCalleeURI( URI calleeURI ) {
        this.calleeURI = calleeURI;
    }

    public String getCalleeRoleName() {
        return calleeRoleName;
    }

    /**
     * Callee leg role name
     *
     * @param calleeRoleName Use one of the role names defined in Leg class
     */
    public void setCalleeRoleName( String calleeRoleName ) {
        this.calleeRoleName = calleeRoleName;
    }


    public Address getCalleeAddress() {
        return calleeAddress;
    }

    public void setCalleeAddress( Address calleeAddress ) {
        this.calleeAddress = calleeAddress;
    }

    public Address getCallerAddress() {
        return callerAddress;
    }

    public void setCallerAddress( Address callerAddress ) {
        this.callerAddress = callerAddress;
    }

    /**
     * Should be one of the B2BUA Servlets loaded in the container
     *
     * @return the b2bua servlet class name which handles the SIP messages
     */
    public String getMachineType() {
        return machineType;
    }

    /**
     * Should be one of the B2BUA Servlets loaded in the container
     *
     * @param machineType class name of some B2BUA servlet, Use one of the constants defined above
     */
    public void setMachineType( String machineType ) {
        this.machineType = machineType;
    }


    public RequestModifier getRequestModifier() {
        return requestModifier;
    }

    public void setRequestModifier( RequestModifier requestModifier ) {
        this.requestModifier = requestModifier;
    }

    public void connect() throws CallServiceException {
        if ( getUASLeg() == null ) {
//            setUASLeg( (UASLeg)getCallService().getLeg( Leg.CALLER ) );
            // Which leg to connect?
            setUASLeg( (UASLeg) getCallService().getTransferLeg() ); // Use the transfer leg as the UAS leg
        }
        SipServletRequest peerInvite = null;
        try {
            if ( getCalleeAddress() != null ) {
                peerInvite = getUASLeg().createRequest( getUASLeg().getInitialRequest(),
                        getCallerAddress(), getCalleeAddress() );
                peerInvite.setRequestURI( getCalleeURI() );
            } else {
                peerInvite = getUASLeg().createRequest( getUASLeg().getInitialRequest(), getCalleeURI() );
            }
            if ( getRequestModifier() != null ) {
                getRequestModifier().modify( peerInvite );
            }
            peerInvite.getSession().setHandler( getMachineType() );
            getUASLeg().getSession().setHandler( getMachineType() );
            getUASLeg().createPeerLeg( peerInvite, getCalleeRoleName() );
            peerInvite.send();
        } catch ( Throwable e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            getUASLeg().setState( LegState.IDLE );
            throw new CallServiceException( e );
        }
    }

    public void involveApplications() throws CallServiceException {
        Application myapp = getCallService().getApplicationContext().getApplication();
        Iterator iter = myapp.getParticipationMap().keySet().iterator();
        while ( iter.hasNext() ) {
            String partyName = (String) iter.next();
            String role = (String) myapp.getParticipationMap().get( partyName );
            try {
                // TODO should be wrapped by a transactional context?
                Leg party = null;
                try {
                    party = getCallService().getLeg( partyName );
                } catch ( IllegalStateException e ) {
                    logger.warning( e.getMessage() );
                    continue;
                }
                if ( role.equalsIgnoreCase( "required" ) ) {
                    myapp.setRequest( getCallService().getInitialRequest() );
                    party.involve( myapp ); //TODO invalid semantics!
                } else if ( role.equalsIgnoreCase( "teardown" ) ) {
                    party.terminate();
                } else {
                    throw new IllegalStateException( "Invalid participation role specified for '" + partyName +
                            "' in application profile for: " + myapp.getName() );
                }
            } catch ( Throwable t ) {
                logger.log( Level.SEVERE, t.getMessage(), t );
                getCallService().getApplicationContext().destroy();
                throw new CallServiceException( t );
            }
        }
        getCallService().getApplicationContext().destroy();
    }

    public void setUASLeg( UASLeg UACLeg ) {
        this.UASLeg = UACLeg;
        setCallerAddress( getUASLeg().getInitialRequest().getFrom() );
    }

    public UASLeg getUASLeg() {
        return this.UASLeg;
    }
}
