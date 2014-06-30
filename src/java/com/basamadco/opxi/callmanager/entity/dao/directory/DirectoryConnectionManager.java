package com.basamadco.opxi.callmanager.entity.dao.directory;

import com.novell.ldap.LDAPConnection;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

/**
 * @author Jrad
 *         Date: Mar 8, 2007
 *         Time: 1:11:30 PM
 */
public interface DirectoryConnectionManager {

    /**
     * Returns a bound connection for the specified DN/PASSWORD from the underlying connection pool
     *
     * @param dn
     * @param password
     * @return a JLDAP ldap connection object 
     * @throws OpxiException
     */
    public LDAPConnection getConnection( String dn, String password ) throws DAOException;

    /**
     * Makes the ldap connection available to other clients
     *
     * @param connection The ldap connection to make free
     */
    public void free( LDAPConnection connection );

}
