package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.RegistrationNotAvailableException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.Proxy;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Nov 15, 2007
 *         Time: 1:44:34 PM
 */
public abstract class ProxyTarget extends CallTarget {

    private static final Logger logger = Logger.getLogger( ProxyTarget.class.getName());


    protected ProxyTarget() {
    }

    protected ProxyTarget( CallTarget target ) {
        super( target );
    }

    public boolean isQueueable() {
        return false;
    }

    public List getTargetURIs() throws OpxiException {
        Set<Registration> contacts = getServiceFactory().getLocationService().findRegistrations(
                new UserAgent( getName(), OpxiToolBox.getLocalDomain() )
        );
        if( contacts.size() > 0 ) {
            return getServiceFactory().getSipService().toURIList( contacts );
        } else {
            throw new RegistrationNotAvailableException( getName() );
        }
    }

    public void service( CallService call ) throws OpxiException {
//        if( call.getState() == CallService.INITIALIZED ) {
            call.setState( CallService.PROXY );
            try {
                Proxy p = call.getInitialRequest().getProxy();
                p.setRecordRoute( OpxiSipServlet.RECORD_ROUTE );
                p.setSupervised( OpxiSipServlet.SUPERVISED );
                p.proxyTo( getTargetURIs() );
                call.getInitialRequest().getSession().setHandler( ApplicationConstants.PROXY_SERVLET );
            } catch ( OpxiException e ) {
                logger.severe( e.getMessage() );
                throw e;
            } catch ( Exception e ) {
                logger.severe( e.getMessage() );
                throw new CallServiceException( e );
            }
//        } else {
//            throw new IllegalStateException( "Can't proxy the call in state '" + call.getStateString() + "' to: " + getTargetURIs() );
//        }
    }


}
