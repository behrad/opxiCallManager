package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.call.AbstractTimerContext;

import javax.servlet.sip.SipServletRequest;
import java.util.logging.Logger;
import java.util.Date;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 5, 2007
 * Time: 4:23:36 AM
 */
public class RegisterTimer extends AbstractTimerContext implements SIPConstants {

    private static final Logger logger = Logger.getLogger( RegisterTimer.class.getName() );

    private static final int REGISTER_TIMER_THRESHHOLD = Integer.parseInt(
            PropertyUtil.getProperty( "opxi.callmanager.sip.registrar.registerTimerThreshHold" ) );

    public static final String REGISTER_TIMER_EXCEEDED = "Registration Timer Exceeded";


    private RegistrationContext registrationContext;

    public RegisterTimer( RegistrationContext registrationContext ) {
        super( registrationContext.getServiceFactory(), registrationContext.getRecentRegisterMessage().getCallId() );
        this.registrationContext = registrationContext;

        setTimer( this.registrationContext.getServiceFactory().getSipService().getTimerService().createTimer(
                this.registrationContext.getRecentRegisterMessage().getApplicationSession(),
                this.registrationContext.getRegistration().getInterval() + REGISTER_TIMER_THRESHHOLD * 1000, false, this
        )
        );
    }

    public SipServletRequest getRegister() {
        return registrationContext.getRecentRegisterMessage();
    }

    public void timeout() throws TimerException {
        logger.finest( "Trying to close user registration since no re-register received for Call-ID = " + getRegister().getCallId() );
        try {
            TimeoutRegistrationEvent event = new TimeoutRegistrationEvent( registrationContext );
            event.service();
        }
        catch ( IOException ex ) {
            logger.warning( " An exception occured during registration timeout. Call-ID = " + getRegister().getCallId() );
            logger.warning( ex.getMessage() );
        }
        catch ( OpxiException ex ) {
            logger.warning( " An exception occured during registration timeout. Call-ID = " + getRegister().getCallId() );
            logger.warning( ex.getMessage() );
        }
        logger.finest( "Closed user registration . Call-ID = " + getRegister().getCallId() );
    }

    public String toString() {
//        return " Agent = '" + getRegister().getTo().getURI() + "' Call-ID = '" + getRegister().getCallId()
//                + "' Timer Expiry = '" + new Date(getTimer().scheduledExecutionTime()) + "'";
        String str = "???";
        if (getTimer() != null) {
            Date dt = new Date( getTimer().scheduledExecutionTime() );
            str = dt.toString();
        }
        return "[Expiry='" + str + "']";
    }
}
