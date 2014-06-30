package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.directory.*;

import java.util.logging.Logger;

public class LdapDAOFactory extends DirectoryDAOFactory {
	
	private static final Logger logger = Logger.getLogger( LdapDAOFactory.class.getName() );
	

    private DirectoryConnectionManager connectionManager;


    public LdapDAOFactory() throws DAOFactoryException {
        try {
            connectionManager = new LdapConnectionManager();
        } catch ( OpxiException e ) {
            throw new DAOFactoryException( e.getMessage(), e );
        }
    }

    /*private LdapConfiguration configureLdap() {
        LdapConfiguration config = new LdapConfiguration();
        config.setVersion( LDAP_VERSION );
        config.setPort( LDAP_PORT );
        config.setHost( LDAP_HOST );
        config.setUsername( LOGIN_DN );
        config.setPassword( LOGIN_PASS );
        return config;
    }*/

    public AgentDAO getAgentDAO() throws DAOException {

        // logger.finest( "Returning new LdapAgentDAO" );
        return new LdapAgentDAO( this, connectionManager );
    }

    public PoolTargetDAO getPoolTargetDAO() throws DAOException {
        return new LdapPoolTargetDAO( this, connectionManager );
    }

    public QueueTargetDAO getQueueTargetDAO() throws DAOException {
        return new LdapQueueTargetDAO( this, connectionManager );
    }

    public CallTargetDAO getCallTargetDAO() throws DAOException {
        return new LdapCallTargetDAO( this, connectionManager, CallTargetCacheManager.getInstance() );
    }

    public TrunkDAO getTrunkDAO() throws DAOException {
        return new LdapTrunkDAO( this, connectionManager);
    }

//    public DirectoryDAO getGenericDAO() throws DAOException {
//        return getAgentDAO();
//    }

}
