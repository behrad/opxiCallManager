package com.basamadco.opxi.callmanager.logging;

import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.activitylog.schema.RegistrarService;

import java.util.Date;
import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 26, 2007
 * Time: 9:52:19 PM
 */
public class AgentRegistrationLogger extends ChildActivityLogger {

    private static final Logger logger = Logger.getLogger( AgentRegistrationLogger.class.getName() );

    public AgentRegistrationLogger( OpxiActivityLogger opxiActivityLogger ) {
        super( opxiActivityLogger );
    }

    private RegistrarService getRegistrarSvc() {
        return parent.getLogVO().getAgentActivity().getRegistrarSvc();
    }

    public void logRegistration( Registration reg ) {
        com.basamadco.opxi.activitylog.schema.Registration registration = addRegistration( reg );
        registration.setContact( reg.getContactURI() );
        registration.setBeginDate( reg.getSubmission() );
        registration.setExpiry( reg.getInterval() / 1000 );
        registration.setProtocol( reg.getTransProtocol() );
        registration.setCtxId( reg.getId() );
    }

    public void logUnregistration( Registration reg ) {
        com.basamadco.opxi.activitylog.schema.Registration registration = getRegistration( reg );

        if ( registration == null ) {
            logger.warning( "No registration entity found in AAL...! " );
            return;
//            logger.finer( "No registration tag found!, adding new one..." );
//            registration = addRegistration( reg );
//            registration.setContact( reg.getContactURI() );
//            registration.setBeginDate( reg.getSubmission() );
//            registration.setExpiry( reg.getInterval() / 1000 );
//            registration.setProtocol( reg.getTransProtocol() );
        }

        registration.setEndDate( new Date() );
        registration.setEndNote( reg.getComment() );
    }

    /*private com.basamadco.opxi.activitylog.schema.Registration getOrAddRegistration( Registration reg ) {
        com.basamadco.opxi.activitylog.schema.Registration registration = getRegistration( reg );
        if (registration != null) return registration;
        return addRegistration( reg );
    }*/

    private com.basamadco.opxi.activitylog.schema.Registration getRegistration( Registration reg ) {
        for ( com.basamadco.opxi.activitylog.schema.Registration regTag : getRegistrarSvc().getRegistration() ) {
            if ( regTag.getCtxId().equals( reg.getId() ) ) {
                return regTag;
            }
            /*if (!reg.getTransProtocol().equalsIgnoreCase( registrations[i].getProtocol() )) continue;
            if (registrations[i].getEndDate() != null) continue;
            if (!reg.getContactURI().equalsIgnoreCase( registrations[i].getContact() )) continue;
            return registrations[i];*/
        }
        return null;
    }

    private com.basamadco.opxi.activitylog.schema.Registration addRegistration( Registration reg ) {
        com.basamadco.opxi.activitylog.schema.Registration registration =
                new com.basamadco.opxi.activitylog.schema.Registration();
        getRegistrarSvc().addRegistration( registration );
        return registration;
    }

    public void transformFrom( com.basamadco.opxi.activitylog.schema.Registration r ) {
        getRegistrarSvc().addRegistration( r );
    }
}
