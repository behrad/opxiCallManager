package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.sip.AbstractContext;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import java.io.IOException;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 5, 2007
 * Time: 11:02:28 PM
 */
public class RegistrationContext implements AbstractContext, SIPConstants, ApplicationConstants {

    private final static Logger logger = Logger.getLogger( RegistrationContext.class.getName() );

    private ServiceFactory serviceFactory;

    private SipServletRequest recentRegisterMessage;

    private Registration registration;

    private RegisterTimer registrationTimer;

    private boolean isNew = true;


    public RegistrationContext( ServiceFactory sf, Registration reg ) {
        serviceFactory = sf;
        this.registration = reg;
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public SipServletRequest getRecentRegisterMessage() {
        return recentRegisterMessage;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration( Registration registration ) {
        this.registration = registration;
    }

    public RegisterTimer getRegistrationTimer() {
        return registrationTimer;
    }

    public boolean isNew() {
        return isNew;
    }

    private void setRecentRegisterMessage( SipServletRequest recentRegisterMessage ) {
        if ( this.recentRegisterMessage != recentRegisterMessage ) {
            removeRequest( this.recentRegisterMessage );
        }
        this.recentRegisterMessage = recentRegisterMessage;
        getRecentRegisterMessage().getApplicationSession().setAttribute( AbstractContext.class.getName(), this );
    }

    public void service( SipServletRequest register ) throws OpxiException {
        try {
            setRecentRegisterMessage( register );
            RegistrarEvent event = createFromMessage();
            isNew = false;
            refreshTimer();
            event.service();
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new OpxiException( e.getMessage() );
        }
    }

    public void preprocess( SipServletRequest register ) throws OpxiException, IOException {
        if ( getRecentRegisterMessage() != null ) {
            if ( !getRecentRegisterMessage().getCallId().equalsIgnoreCase( register.getCallId() ) ) {
                // TODO this logic is in code-level. Make it's business logic clear...
                logger.finest( "Registration request looks to be for the previous registration. " +
                        "PERV-CALL-ID = " + getRecentRegisterMessage().getCallId() +
                        " NEW-CALL-ID = " + register.getCallId() );
                new RecoverRegisterEvent( this, recentRegisterMessage.getCallId() ).service();
                isNew = true;
            }
        }
    }

    private RegistrarEvent createFromMessage() throws OpxiException, ServletException {
        if ( RegisterUtility.isAllRemoval( getRecentRegisterMessage() ) ) {// Unregister
            logger.finer( "Registration request approved as an un-register request. [Context-ID = '" + getRegistration() + "']" );
            //TODO : u have to do sth
            return new UnregisterEvent( this );
        } else if ( RegisterUtility.isCertainRemoval( getRecentRegisterMessage() ) ) {// Unregister
            logger.finer( "Registration request approved as an un-register request. [Context-ID = '" + getRegistration() + "']" );
            return new UnregisterEvent( this );
        } else {
            if ( isNew() ) { // Register
                logger.finer( "Registration request approved as a register request. [Context-ID = '" + getRegistration() + "']" );
                return new RegisterEvent( this );
            } else {// Refresh-register
                logger.finer( "Registration request approved as a refresh-register request. [Context-ID = '" + getRegistration() + "']" );
                return new ReRegisterEvent( this );
            }
        }
    }

    public void sendWelcomeMessage() {
        ListIterator allowHeaders = getRecentRegisterMessage().getHeaders( ALLOW );
        while ( allowHeaders.hasNext() ) {
            String header = (String) allowHeaders.next();
            if ( header.trim().equalsIgnoreCase( MESSAGE ) ) {
                getServiceFactory().getSipService().sendIM( getRegistration(),
                        ResourceBundleUtil.getMessage( "callmanager.registrar.hello",
                                getRegistration().getUserAgent().getName() ), 3 );
                getServiceFactory().getSipService().sendIM( getRegistration(),
                        ResourceBundleUtil.getMessage( "callmanager.registrar.welcome",
                                OpxiToolBox.getLocalDomain() ), 3 );

            }
        }
    }

    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof RegistrationContext) ) return false;
        RegistrationContext regContext = (RegistrationContext) o;
        if ( getRegistration() != regContext.getRegistration() ) return false;
        return true;
    }

    public int hashCode() {
        return registration.hashCode();
    }

    private void addTimer() {
        registrationTimer = new RegisterTimer( this );
    }

    private void refreshTimer() {
        removeTimer();
        addTimer();
    }

    private void removeRequest( SipServletRequest request ) {
        if ( request == null ) return;
        try {
            request.getApplicationSession().removeAttribute( AbstractContext.class.getName() );
            request.getApplicationSession().invalidate();
        } catch ( Exception e ) {
            logger.severe( "+++++++++++++++++++++++++++++++ Error invalidating old request: " + request );
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    private void removeTimer() {
        if ( registrationTimer == null ) return;
        registrationTimer.cancel();
    }

    public void destroy() {
        logger.finest( "Destroying registration context: '" + registration + "'" );
        removeTimer();
        removeRequest( getRecentRegisterMessage() );
    }

    public String toString() {
        return "[Context-ID='"
                + registration
                + "' , Call-ID='"
                + (recentRegisterMessage == null ? "Faked registration." : recentRegisterMessage.getCallId())
                + "' , "
                + (getRegistrationTimer() == null ? "['No Timer Defined']" : getRegistrationTimer().toString())
                + "']";
    }

}
