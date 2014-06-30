package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.OpxiException;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Jan 19, 2008
 * Time: 9:24:13 PM
 */
public class TimeoutRegistrationEvent extends RegistrarEvent {

    private static final Logger logger = Logger.getLogger(TimeoutRegistrationEvent.class.getName());

    public TimeoutRegistrationEvent(RegistrationContext registrationContext) {
        super(registrationContext);
    }

    public void service() throws OpxiException, IOException {
        logger.info("Timeout registration event: '" + getRegistration() + "'");
        getRegistration().setComment("Timed out registration.");
        try {
            getServiceFactory().getLocationService().handleContactUnregistration(getRegistration());
        } finally {
            getServiceFactory().getRegistrarService().removeContextFor(getRegistration());
        }
    }

}
