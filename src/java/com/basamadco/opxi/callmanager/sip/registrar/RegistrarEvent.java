package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.Registration;

import javax.servlet.sip.SipServletRequest;
import java.util.logging.Logger;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 10, 2007
 * Time: 11:01:05 PM
 */
public abstract class RegistrarEvent implements SIPConstants, ApplicationConstants {



    private RegistrationContext context;


    public abstract void service() throws OpxiException, IOException;

//    public abstract void doResponse() throws IOException;

    public RegistrarEvent(RegistrationContext context ) {
        this.context = context;
    }

    protected RegistrationContext getContext() {
        return context;
    }

    protected ServiceFactory getServiceFactory() {
        return getContext().getServiceFactory();
    }

    protected SipServletRequest getRegister() {
        return getContext().getRecentRegisterMessage();
    }

    protected Registration getRegistration() {
        return getContext().getRegistration();
    }
}
