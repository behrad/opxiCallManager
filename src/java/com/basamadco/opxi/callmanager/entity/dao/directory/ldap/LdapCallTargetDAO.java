package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.directory.CallTargetDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.pool.Agent;
import com.basamadco.opxi.callmanager.call.*;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jul 26, 2006
 *         Time: 2:19:53 PM
 */
public class LdapCallTargetDAO extends LdapDAO implements CallTargetDAO {

    private final static Logger logger = Logger.getLogger( LdapCallTargetDAO.class.getName() );


    private CallTargetCacheManager callTargetCache;

    protected String[] fields() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public LdapCallTargetDAO( DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager, CallTargetCacheManager callTargetCache ) {
        super( factory, connectionManager );
        this.callTargetCache = callTargetCache;
    }

    public CallTarget getCallTargetById( String dn ) throws DAOException, EntityNotExistsException {
        return getCallTargetByName( getCNForDN( dn ) );
    }

    public CallTarget getCallTargetByName( String name ) throws DAOException, EntityNotExistsException {

        DirectoryEntity entity = null;
        try {
            LdapAgentDAO ldapAgentDAO = new LdapAgentDAO( super.factory, super.connectionManager );
            entity = ldapAgentDAO.read( name );
            logger.finest( " Found a call target from directory server: " + AGENT_SEARCH_BASE );
        }
        catch ( EntityNotExistsException e1 ) {
            try {
                LdapSkillDAO ldapSkillDAO = new LdapSkillDAO( super.factory, super.connectionManager, callTargetCache );
                entity = ldapSkillDAO.read( name );
                logger.finest( " Found a call target from directory server: " + SKILL_SEARCH_BASE );
            }
            catch ( EntityNotExistsException e2 ) {
                try {
                    LdapGroupDAO ldapGroupDAO = new LdapGroupDAO( super.factory, super.connectionManager, callTargetCache );
                    entity = ldapGroupDAO.read( name );
                    logger.finest( " Found a call target from directory server: " + GROUP_SEARCH_BASE );
                }
                catch ( EntityNotExistsException e3 ) {
                    try {
                        LdapServiceDAO ldapServiceDAO = new LdapServiceDAO( super.factory, super.connectionManager, callTargetCache );
                        entity = ldapServiceDAO.read( name );
                        logger.finest( " Found a call target from directory server: " + SERVICE_SEARCH_BASE );
                    }
                    catch ( EntityNotExistsException e4 ) {
                        try {
                            LdapTrunkDAO ldapTrunkDAO = new LdapTrunkDAO( super.factory, super.connectionManager );
                            entity = ldapTrunkDAO.read( name );
                            logger.finest( " Found a call target from directory server. " + TRUNK_SEARCH_BASE );
                        }
                        catch ( EntityNotExistsException e5 ) {
                            entity = read( name );
                            logger.finest( " Found a call target from directory server: " + entity );
                        }
                    }
                }
            }
        }
        return readSpecific( entity );
//        DirectoryEntity entity = read(name);
//        return readSpecific(entity);
    }

    public CallTarget getCallTargetByPhoneNumber( String phoneNumber ) throws DAOException, EntityNotExistsException {
        CallTarget ct = callTargetCache.lookUpCacheFor( phoneNumber );
        if ( ct != null ) {
            return ct;
        } else {
            DirectoryEntity entity = searchByPhoneNumber( phoneNumber );
            ct = readSpecific( entity );
            callTargetCache.updateCacheByKey( phoneNumber, ct );
            return ct;
        }
    }

    public CallTarget getCallTargetByPattern
            ( String
                    pattern ) throws DAOException, EntityNotExistsException {
        DirectoryEntity entity = searchByPattern( pattern );
        if ( entity == null ) {
            throw new EntityNotExistsException( "no matching for pattern '" + pattern + "'" );
        }
        CallTarget ct = readSpecific( entity );
        return ct;
    }

    private CallTarget readSpecific( DirectoryEntity entity ) throws DAOException, EntityNotExistsException {
        if ( entity.isSkill() || entity.isGroup() ) {
            return (QueueTarget) ((LdapDAOFactory) getDAOFactory()).getQueueTargetDAO().read( entity.getCN() );
        } else if ( entity.isApplication() ) {
            String url = getAttributeValue( entity.getCN(), "keywords" );
            Application app;
            if ( url.indexOf( "SIP_URI:" ) > -1 ) {
                app = new SipApplication();
                app.setUrl( url.split( "SIP_URI:" )[1] );
            } else {
                app = new Application();
                app.setUrl( url.split( "URL:" )[1] );
            }
            app.setDN( entity.getDN() );
            app.setCN( entity.getCN() );
            app.setTelephoneNumber( getAttributeValue( entity.getCN(), "telephoneNumber" ) );
            return app;
        } else if ( entity.isAgent() ) {
            return (Agent) ((LdapDAOFactory) getDAOFactory()).getAgentDAO().read( entity.getCN() );
        } else if ( entity.isTrunk() ) {
//            System.out.println( "read TRUNK: " + entity.getCN() );
            return (Trunk) ((LdapDAOFactory) getDAOFactory()).getTrunkDAO().read( entity.getCN() );
        }
        throw new DAOException( entity + " type not identified." );
    }

    public String[] listAllByCNs
            () throws DAOException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDN
            ( String
                    CN ) throws DAOException {
        return getAttributeValue( CN, "dn" );
    }

    public String getDN
            ( DirectoryEntity
                    entity ) throws DAOException {
        throw new IllegalAccessError( "Don't use getDN!" );
    }

}