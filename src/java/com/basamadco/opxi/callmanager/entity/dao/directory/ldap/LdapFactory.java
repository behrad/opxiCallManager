package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.security.Security;

/**
 * Is an DIRECTORY client configuration repository.
 * Following attributes should be set in OpxiCallManager properties file:<br>
 * 1."opxi.ldap.host.name": DIRECTORY server host name<br>
 * 2."opxi.ldap.host.port": DIRECTORY server host port<br>
 * 3."opxi.ldap.admin.username": DIRECTORY administrator username<br>
 * 4."opxi.ldap.admin.passwd": DIRECTORY administrator password
 * 
 * @author Jrad
 *
 * @deprecated
 */
public abstract class LdapFactory {
	
	private static final Logger logger = Logger.getLogger( LdapFactory.class.getName() );
	
	/**
	 * DIRECTORY server version which is used by the client
	 */
	private static final int LDAP_VERSION = LDAPConnection.LDAP_V3;
	
	/**
	 * DIRECTORY server host name
	 */
    private static final String LDAP_HOST = PropertyUtil.getProperty( "opxi.ldap.host.name" );
    
    /**
     * DIRECTORY server port to connect to
     */
    private static final int    LDAP_PORT = Integer.parseInt( PropertyUtil.getProperty( "opxi.ldap.host.port" ) );
    
    /**
     * DIRECTORY server administrator login name
     */
    private static final String LOGIN_DN  = PropertyUtil.getProperty( "opxi.ldap.admin.username" );
    
    /**
     * DIRECTORY server administrator login password
     */
    private static final String LOGIN_PASS = PropertyUtil.getProperty( "opxi.ldap.admin.passwd" );
    
    static {
    	Security.addProvider( new com.novell.sasl.client.SaslProvider() );
    }
	
    /**
     * Returns a configured DIRECTORY client
     * @return LdapManager object
     * @throws OpxiException
     */
	/*public static LdapManager getLdapManager() throws OpxiException {
		LDAPConnection lc  = new LDAPConnection();
		try {		
			lc.connect( LDAP_HOST, LDAP_PORT ); 
        	lc.bind( LDAP_VERSION, LOGIN_DN, LOGIN_PASS.getBytes() );
        	return new LdapManager(lc);
        } catch( LDAPException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        	throw new OpxiException( "Couldn't initialze LdapManager", e );
        }
	}*/
	
}
