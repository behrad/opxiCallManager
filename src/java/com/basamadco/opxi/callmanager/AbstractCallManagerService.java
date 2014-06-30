package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

import java.util.List;

/**
 * This is the common base class for OPXi Call Manager services
 * @author Jrad
 *         Date: Apr 6, 2006
 *         Time: 1:45:10 PM
 */
public abstract class AbstractCallManagerService implements CallManagerServiceInterface, SIPConstants, ApplicationConstants {

    private ServiceFactory factory;


    public void setServiceFactory( ServiceFactory factory ) {
        this.factory = factory;
    }

    public ServiceFactory getServiceFactory() {
        if( factory == null ) {
            throw new IllegalStateException( "No ServiceFactory is set in service: " + this.getClass().getName() );
        }
        return factory;
    }

    public List listObjects() {
        throw new IllegalStateException( "Operation not available..." );
    }

    public void destroy() {
    }

}