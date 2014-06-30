package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.entity.Domain;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.sip.presence.CallManagerPresence;
import com.basamadco.opxi.callmanager.sip.registrar.RegistrationNotFoundException;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.util.LockManager;

import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipURI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * OPXi Call Manager core location service implementation.
 *
 * @author Jrad
 */
public class LocationService extends AbstractCallManagerService {

    private static final Logger logger = Logger.getLogger( LocationService.class.getName() );


    private final Map<UserAgent, Set<Registration>> registrationTable = new ConcurrentHashMap<UserAgent, Set<Registration>>();


    private final Map<Registration, Set<String>> contactRefCounter = new ConcurrentHashMap<Registration, Set<String>>();


    private final static String REGISTRATION_TABLE_LOCK_ID = "LS.REGISTRATION_TABLE_LOCK";

    public LocationService( ServiceFactory sf ) {
        super();
        setServiceFactory( sf );
//        registerOpxiCallManager();
    }

    protected void registerOpxiCallManager() {
        try {
            Address addr = getServiceFactory().getSipService().getSipFactory().createAddress(
                    CallManagerPresence.CALL_MANAGER_URI
            );
            Registration reg = new Registration( CallManagerPresence.CALL_MANAGER_UA, addr );

            reg.setComment( "OpxiCallManager Server Local Registration" );
            addOrUpdateRegistration( reg );
        } catch ( Exception e ) {
            logger.severe( e.getMessage() );
        }
    }

    public void handleContactRegistration( Registration contact ) throws OpxiException {
        logger.finest( "Register contact: '" + contact + "'" );
        addOrUpdateRegistration( contact );
        try {
            getServiceFactory().getAgentService().onLogin( contact );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            removeRegistration( contact );
            throw e;
        }

    }

    public void handleRefreshContact( Registration contact ) throws OpxiException {
        logger.finest( "Refresh contact: '" + contact + "'" );
        synchronized ( LockManager.getLockById( REGISTRATION_TABLE_LOCK_ID ) ) {
            if ( registrationTable.containsKey( contact.getUserAgent() ) ) {
                registrationTable.get( contact.getUserAgent() ).remove( contact );
                registrationTable.get( contact.getUserAgent() ).add( contact );
            } else {
                throw new RegistrationNotFoundException( contact.toString() );
            }
        }
    }

    public void handleRecoverRegister( Registration contact, String prev_callId ) throws OpxiException {
        removeReference( contact, prev_callId );
        getServiceFactory().getAgentService().logLoggedOffContact( contact );
    }

    public void handleContactUnregistration( Registration contact ) throws OpxiException {
        logger.finest( "UnRegistering contact: '" + contact + "'" );
        getServiceFactory().getAgentService().onLogoff( contact );
        removeRegistration( contact );
    }

    private boolean addOrUpdateRegistration( Registration contact ) throws OpxiException {
        synchronized ( LockManager.getLockById( REGISTRATION_TABLE_LOCK_ID ) ) {
            addContactReference( contact );
            if ( registrationTable.containsKey( contact.getUserAgent() ) ) {
                return addContact( contact );
            } else {
                Set<Registration> contacts = new HashSet<Registration>();
                contacts.add( contact );
                registrationTable.put( contact.getUserAgent(), contacts );
//                getServiceFactory().getAgentService().loadAgent( contact.getUserAgent() );
                return true;
            }
        }
    }

    private boolean addContact( Registration contact ) {
        Set<Registration> UAsContacts = registrationTable.get( contact.getUserAgent() );
        if ( UAsContacts.contains( contact ) ) {
            // update old contact
            UAsContacts.remove( contact );
            UAsContacts.add( contact );
            return false;
        }
        return UAsContacts.add( contact );

    }

    private void addContactReference( Registration contact ) {
        if ( !contactRefCounter.containsKey( contact ) ) {
            Set<String> keys = new HashSet<String>();
            contactRefCounter.put( contact, keys );
        }
        contactRefCounter.get( contact ).add( contact.getId() );
    }

    private boolean removeRegistration( Registration contact ) throws OpxiException {
        synchronized ( LockManager.getLockById( REGISTRATION_TABLE_LOCK_ID ) ) {
            if ( registrationTable.containsKey( contact.getUserAgent() ) ) {
                if ( contactRefCounter.containsKey( contact ) ) {
                    contactRefCounter.get( contact ).remove( contact.getId() );
                    if ( contactRefCounter.get( contact ).size() == 0 ) {

                        contactRefCounter.remove( contact );

                        boolean moved = registrationTable.get( contact.getUserAgent() ).remove( contact );
                        if ( moved && registrationTable.get( contact.getUserAgent() ).size() == 0 ) {
                            registrationTable.remove( contact.getUserAgent() );
                            getServiceFactory().getAgentService().unloadAgent( contact );
                        }
                        return moved;
                    }
                }
                return false;
            } else {
                logger.warning( "No registered contact exists, '" + contact + "'" );
                return false;
            }
        }
    }

    private void removeReference( Registration contact, String callId ) {
        if ( registrationTable.containsKey( contact.getUserAgent() ) ) {
            contactRefCounter.get( contact ).remove( callId );
            if ( contactRefCounter.get( contact ).size() == 0 ) {
                contactRefCounter.remove( contact );
            }
        }
    }

    /**
     * Lists all Registration bindings exists for the specified
     * user agent in the location service.
     *
     * @param ua UserAgent object
     * @return a list of Registration bindings
     * @throws OpxiException
     */
    public Set<Registration> findRegistrations( UserAgent ua ) throws RegistrationNotFoundException {
        /*synchronized ( LockManager.getLockById( REGISTRATION_TABLE_LOCK_ID ) ) {
            if ( registrationTable.containsKey( ua ) ) {
                Set<Registration> copy = new HashSet<Registration>();
                copy.addAll( registrationTable.get( ua ) );
                return copy;
            } else {
                throw new RegistrationNotFoundException( ua.getSipURIString() );
            }
        }*/
        return findRegistrationsByContact( ua );
    }

    public Set<Registration> findRegistrationsByContact( UserAgent ua ) throws RegistrationNotFoundException {
        synchronized ( LockManager.getLockById( REGISTRATION_TABLE_LOCK_ID ) ) {
            if ( registrationTable.containsKey( ua ) ) {
                Map<String, Registration> contactsMap = new HashMap<String, Registration>();
                Iterator<Registration> regs = registrationTable.get( ua ).iterator();
                while ( regs.hasNext() ) {
                    Registration reg = regs.next();
                    String host = ((SipURI) reg.getLocation().getURI()).getHost();
//                    if( !contactsMap.containsKey( host ) ) {
                    contactsMap.put( host, reg );
//                    }
                }
                Set<Registration> copy = new HashSet<Registration>();
                copy.addAll( contactsMap.values() );
                return copy;
            } else {
                throw new RegistrationNotFoundException( ua.getSipURIString() );
            }
        }
    }

    public boolean isRegisteredContact( Address contact ) {
        synchronized ( LockManager.getLockById( REGISTRATION_TABLE_LOCK_ID ) ) {
            UserAgent contactUA = new UserAgent( contact );
            // TODO kalak rashti! since contact is IP based not DNS based
            contactUA.setDomain( new Domain( ApplicationConstants.DOMAIN ) );
            if ( registrationTable.containsKey( contactUA ) ) {
                Registration contactReg = new Registration( contactUA, contact );
                return checkIfRegistered( registrationTable.get( contactUA ), contactReg );
            }
            return false;
        }
    }

    private boolean checkIfRegistered( Set<Registration> registrations, Registration keyContact ) {
        for ( Registration contact : registrations ) {
            if ( contact.equalsIgnoreSubclasses( keyContact ) ) {
                return true;
            }
        }
        return false;
    }

    public String[] getAssociatedContextIds( Registration contact ) {
        String[] keys = new String[contactRefCounter.get( contact ).size()];
        return contactRefCounter.get( contact ).toArray( keys );
    }

    public Collection<Registration> getAllRegistrations() {
        synchronized ( LockManager.getLockById( REGISTRATION_TABLE_LOCK_ID ) ) {
            List<Registration> all = new ArrayList<Registration>();
            for ( Set<Registration> regs : registrationTable.values() ) {
                all.addAll( regs );
            }
            return all;
        }
    }

    public List listObjects() {
        return Arrays.asList( registrationTable.values().toArray() );
    }

    public void destroy() {
        registrationTable.clear();
        contactRefCounter.clear();
        logger.info( "LocationService destroyed successfully." );
    }

}