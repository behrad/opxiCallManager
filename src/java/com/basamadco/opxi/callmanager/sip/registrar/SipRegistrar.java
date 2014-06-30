package com.basamadco.opxi.callmanager.sip.registrar;

import com.basamadco.opxi.callmanager.AbstractCallManagerService;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.sip.UAC;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 6, 2008
 *         Time: 1:26:52 PM
 */
public class SipRegistrar extends AbstractCallManagerService {

    private static final Logger logger = Logger.getLogger( SipRegistrar.class.getName() );

    private Map<Registration, RegistrationContext> registrationContexts = new ConcurrentHashMap<Registration, RegistrationContext>();

    public SipRegistrar( ServiceFactory sf ) {
        super();
        setServiceFactory( sf );
    }

    public void service( SipServletRequest register ) throws OpxiException {

        try {
            UAC uac = new UAC(
                        register.getTo(),
                        RegisterUtility.validateExpiries(
                                register.getAddressHeaders( CONTACT ),
                                register.getExpires()
                        )
                );

            String user = ( (SipURI) register.getFrom().getURI() ).getUser();
            String domain = ( (SipURI) register.getFrom().getURI() ).getHost();

            if (domain.equals( OpxiToolBox.getLocalIP() )) {
                domain = DOMAIN;
            }
            UserAgent ua = new UserAgent( user, domain );

            for( Address contact : uac.getLocations() ) {
                Date now = new Date();
                Registration registration = new Registration( ua, contact
                        , now
                        , new Date( now.getTime() + contact.getExpires() * 1000 )
                );
                registration.setId( register.getCallId() );
                RegistrationContext ctx = resolveRegistrationContext( registration );
                if( ctx == null ) {
                    ctx = new RegistrationContext( getServiceFactory(), registration );
                    registrationContexts.put( registration, ctx );
                }
                ctx.preprocess( register );
                ctx.setRegistration( registration );
                ctx.service( register );
            }
        } catch( Exception e ) {
            throw new OpxiException( e );
        }
    }


    public boolean removeContextFor( Registration registration ) {
        logger.finest( "Removing registration context: '" + registration + "'" );
        RegistrationContext ctx = registrationContexts.remove( registration );
        if( ctx == null ) {
            logger.warning( "No registration context exists to remove! " );
            return false;
        }
        ctx.destroy();
        return true;
    }

    
    private RegistrationContext resolveRegistrationContext( Registration registration ) {
        return registrationContexts.get( registration );
    }

    public List listObjects() {
        List list = new ArrayList();
        for( RegistrationContext ctx : registrationContexts.values() ) {
            list.add( ctx );
        }
        return list;
    }

    public void destroy() {
        for(RegistrationContext ctx : registrationContexts.values() ) {
            ctx.destroy();
        }
        registrationContexts.clear();
        super.destroy();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
