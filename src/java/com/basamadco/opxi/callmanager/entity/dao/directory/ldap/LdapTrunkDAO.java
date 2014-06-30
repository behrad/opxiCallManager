package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.dao.directory.TrunkDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.call.Trunk;

import java.util.logging.Logger;

/**
 * User: AM
 * Date: Sep 12, 2007
 * Time: 4:11:39 PM
 */
public class LdapTrunkDAO extends LdapDAO implements TrunkDAO {

    protected static final Logger logger = Logger.getLogger( LdapAgentDAO.class.getName() );

    private static final String[] FIELDS = {"keywords"};

    public LdapTrunkDAO( DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager ) {
        super( factory, connectionManager );
    }

    protected String[] fields() {
        return FIELDS;
    }

    public String getDN( String CN ) throws DAOException {
        return new StringBuffer()
                .append( "CN=" )
                .append( CN )
                .append( ",CN=" )
                .append( TRUNK_TREE )
                .append( ',' )
                .append( LDAP_SEARCH_BASE )
                .toString();
    }

    public String getDN( DirectoryEntity entity ) throws DAOException {
        if (!( entity instanceof Trunk )) {
            throw new DAOException( "Specified entity was not a Trunk instance" );
        }
        Trunk trunk = (Trunk) entity;
        return getDN( trunk.getName() );
    }

    public String[] listAllByCNs() throws DAOException {
        return null;
    }

    public String getDialPattern( String strPattern ) throws DAOException {
        String searchFilter = "(&(objectClass=serviceConnectionPoint)(keywords=dialPattern:" + strPattern + "))";
        //String searchFilter = "(&(objectClass=serviceConnectionPoint)(dialPattern=" + strPattern + "))";
        return getAttributeValue( searchFilter, LDAP_USER_SIP_ATTRIBUTE );
    }

    public String getStaticRoute( String strSR ) throws DAOException {
        String searchFilter = "(&(objectClass=serviceConnectionPoint)(keywords=staticRoute:" + strSR + "))";
        return getAttributeValue( searchFilter, LDAP_USER_SIP_ATTRIBUTE );
    }

    public String getAttributeValue( String CN, String attr_name ) throws DAOException {
        String searchFilter = "(&(objectClass=serviceConnectionPoint)(cn=" + CN + "))";
        return _getAttributeValue( searchFilter, attr_name );
    }

    public DirectoryEntity read( String cn ) throws EntityNotExistsException, DAOException {
        Trunk entity = new Trunk();
        String filter = "(&(objectClass=serviceConnectionPoint)(cn=" + cn + "))";
        read( filter, FIELDS, entity, getSearchBase() );
        return entity;
    }

    public void read( String cn, DirectoryEntity entity ) throws EntityNotExistsException, DAOException {
        String filter = "(&(objectClass=serviceConnectionPoint)(cn=" + cn + "))";
        read( filter, FIELDS, entity, null );
    }
}
