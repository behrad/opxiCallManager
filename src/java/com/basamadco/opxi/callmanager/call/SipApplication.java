package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.sip.registrar.RegistrationNotFoundException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.Registration;

import javax.servlet.sip.SipURI;
import javax.servlet.sip.ServletParseException;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: Aug 1, 2009
 *         Time: 3:40:42 PM
 */
public class SipApplication extends Application {


    private static final Logger logger = Logger.getLogger( SipApplication.class.getName() );

    /**
     * Constructs the actual access URI for this voice application
     *
     * @return The access URI on the Call Manager's media server
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
    public List getTargetURIs() throws OpxiException {
        Set<Registration> locs = getServiceFactory().getLocationService().findRegistrations( new UserAgent( getUrl() ) );
        for ( Registration loc : locs ) {
            setParameters( (SipURI) loc.getLocation().getURI() );
            return getServiceFactory().getSipService().toURIList( loc.getLocation().getURI() );
        }
        throw new RegistrationNotFoundException( "Application URI not registered: " + getUrl() );
    }


    protected void addProfileParameters( Map<String, String> paramMap ) {
        try {
            SipURI uri = (SipURI) getServiceFactory().getSipService().getSipFactory().createURI( getUrl() );
            Iterator params = uri.getParameterNames();
            while ( params.hasNext() ) {
                String paramName = (String) params.next();
                paramMap.put( paramName, uri.getParameter( paramName ) );
            }
        } catch ( ServletParseException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

}
