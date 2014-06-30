package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.novell.ldap.*;
import com.novell.ldap.util.Base64;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * LdapManager class is a minimal ldap client which serves OpxiCallManager
 * DIRECTORY access needs.<br>
 * An LdapManager instance can be initiated with <i>LdapFactory.getLdapManager()</i> method.<br>
 * Note: Remember to clean your mess when done with LdapManager by calling <link>dispose()</link> method.
 *  
 * @author Jrad
 *
 */
public final class LdapManager {
	
	private final static Logger logger = Logger.getLogger( LdapManager.class.getName() );
	
	private LDAPConnection connection;
	
	/**
	 * Attribute name under which user passwords are stored in DIRECTORY server
	 */
	private static final String LDAP_USER_PASS_ATTRIBUTE = PropertyUtil.getProperty( "opxi.ldap.passwd.attribute" );
	
	/**
	 * Attribute name under which user phone numbers are stored in DIRECTORY server
	 */
	private static final String LDAP_USER_PHONE_ATTRIBUTE = PropertyUtil.getProperty( "opxi.ldap.phone.attribute" );	
	
	/**
	 * Attribute name to be returned as a user's sip username 
	 */
	private static final String LDAP_USER_SIP_ATTRIBUTE = PropertyUtil.getProperty( "opxi.ldap.user.sip.attribute" );
	
	/**
	 * Search base attribute name which OpxiCallManager searches the DIRECTORY server for
	 */
	private static final String LDAP_SEARCH_BASE = PropertyUtil.getProperty( "opxi.ldap.ldapSuffix" );
	
	
	public LdapManager( LDAPConnection conn ) {
		this.connection = conn;
	}
	
	
	/**
     * Does the tricky user password retreival from the 
     * OpxiCallManager configured DIRECTORY server.
     *   
     * @param username DIRECTORY User name
     * @return User's password
     * @throws OpxiException If any error occurs while
     * binding to DIRECTORY server.
     */
    public String getUserPassword( String username ) throws OpxiException {
    	String searchFilter = "(&(objectClass=user)("+LDAP_USER_SIP_ATTRIBUTE+"=" + username + "))";
        return getAttribute( searchFilter, LDAP_USER_PASS_ATTRIBUTE );
    }
    
    /**
     * Retreives the username information from DIRECTORY server for specified phone number
     * 
     * @param phoneNumber User's phone number
     * @return User's DIRECTORY username
     * @throws OpxiException
     */
    public String getUsernameFor( String phoneNumber ) throws OpxiException {    	
    	String searchFilter = "(&(objectClass=user)("+LDAP_USER_PHONE_ATTRIBUTE+"=" + phoneNumber + "))";                   
    	return getAttribute( searchFilter, LDAP_USER_SIP_ATTRIBUTE );
    }
    
    /**
     * Retreives an attribute value from the DIRECTORY server for specified
     * user principal name and attribute name.
     *  
     * @param searchFilter User Principal
     * @param attr_name Attribute name to be read
     * @return String value of the attribute
     * @throws OpxiException
     */
    private String getAttribute( String searchFilter, String attr_name ) throws OpxiException {
//        logger.debug( "&&&&&&&&&&&&&&&&&&&&&& Attribute name: " + attr_name );
        String Value = "";
        try {
	        LDAPSearchResults searchResults = connection.search(
                    LDAP_SEARCH_BASE, LDAPConnection.SCOPE_SUB , searchFilter, new String[] {attr_name}, false );
	        while ( searchResults.hasMore()) {
	            LDAPEntry nextEntry = null;
	            try {
	                nextEntry = searchResults.next();
	            }
	            catch(LDAPException e) {                 
	            	continue;
	            }
	            LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
	            Iterator allAttributes = attributeSet.iterator();                
	            while(allAttributes.hasNext()) {
	                LDAPAttribute attribute = (LDAPAttribute)allAttributes.next();                
	                Enumeration allValues = attribute.getStringValues();
	                if( allValues != null) {
	                    while(allValues.hasMoreElements()) {
	                        Value = (String) allValues.nextElement();
	                        if (Base64.isLDIFSafe(Value)) {
	                            // is printable                             
	                        }
	                        else {
	                            // base64 encode and then print out
	                            Value = Base64.encode(Value.getBytes());                                
	                        }
	                    }
	                }
	            }
	        }
//            logger.debug( "&&&&&&&&&&&&&&&&& value: " + Value );
            return Value;
        } catch( LDAPException e ) {
        	throw new OpxiException( "Error searching ldap", e );
        }
    }
	
    /**
     * Disconnects LdapManager from the DIRECTORY server
     * @throws OpxiException
     */
	public void dispose() throws OpxiException {
		try {
			connection.disconnect();
		} catch( LDAPException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
			throw new OpxiException( "Error disconnecting ldap connection", e );
		}
	}
	
}
