package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.QueueTargetDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;
import com.basamadco.opxi.callmanager.call.QueueTarget;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 24, 2006
 *         Time: 1:46:16 PM
 */
public class LdapQueueTargetDAO extends LdapDAO implements QueueTargetDAO  {

    private static final Logger logger = Logger.getLogger( LdapQueueTargetDAO.class.getName() );

    private static final String[] FIELDS =
            { "telephoneNumber", "displayName",  };
//            { "telephoneNumber", "displayName", "soundUrl", "maxLength-queue", "maxTime-queue" };

    protected String[] fields() {
        return FIELDS;
    }

    protected String getMappedProperty( String attributeName ) {
        /*if( attributeName.equalsIgnoreCase( FIELDS[2] ) ) {
            return "waitingMsgURI";
        }
        if( attributeName.equalsIgnoreCase( FIELDS[3] ) ) {
            return "queueDepth";
        }
        if( attributeName.equalsIgnoreCase( FIELDS[4] ) ) {
            return "maxWaitTime";
        }*/
        return super.getMappedProperty( attributeName );
    }

    public String getDN( DirectoryEntity entity ) throws DAOException {
        throw new IllegalAccessError( "Don't use getCN!" );
//        if (!(entity instanceof QueueTarget )) {
//            throw new DAOException( "Specified dto was not an Group instance" );
//        }
//        QueueTarget group = (QueueTarget)entity;
//        return getCN( group.getName() );
    }

    public String getDN( String CN ) throws DAOException {
        return getAttributeValue( CN, "dn" );
    }

//    public String getSearchBase() {
//        return new StringBuffer()
//        .append("CN=")
//        .append( SKILL_TREE )
////        .append( GROUP_TREE )
//        .append(',')
//        .append( LDAP_SEARCH_BASE ).toString();
//    }

    public LdapQueueTargetDAO( DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager ) {
        super( factory, connectionManager );
    }

    public DirectoryEntity read( String cn ) throws EntityNotExistsException, DAOException {
        QueueTarget target = new QueueTarget();
        String filter = "(&(objectClass=group)(cn=" + cn + "))";
        read( filter, FIELDS, target, LDAP_SEARCH_BASE );
        return target;
    }

//    public void read( String cn, DirectoryEntity entity ) throws EntityNotExistsException, DAOException {
//        String filter = "(&(objectClass=group)(cn=" + cn + "))";
//        read( filter, FIELDS, entity, null );
//    }

    public String[] listAllByCNs() throws DAOException {
//		String filter = "(objectClass=group)";
//		List groups = new ArrayList();
//		List group_names = (List)search( filter, new String[] {"name"} );
//		for( int i = 0; i < group_names.size(); i++ ) {
//			String group_name = (String)group_names.get( i );
//			groups.add( new Group( group_name ) );
//		}
        return null;
    }

    public String getAttributeValue( String CN, String attr_name ) throws DAOException {
        String searchFilter = "(&(objectClass=group)(cn=" + CN + "))";
        return _getAttributeValue( searchFilter, attr_name );
    }

    public String[] getAttributeValues( String CN, String attr_name ) throws DAOException {
        String searchFilter = "(&(objectClass=group)(cn=" + CN + "))";
        return _getAttributeValues( searchFilter, attr_name );
    }

//    public String getMatchingRule( String cn ) throws DAOException {
//        String filter = "(&(objectClass=group)(cn=" + cn + "))";
//        return _getAttributeValue( filter, "matchingRule" );
//    }

    public float getPSSC( String CN ) throws DAOException {
        String filter = "(&(objectClass=group)(cn=" + CN + "))";
        return Float.parseFloat( _getAttributeValue( LDAP_SEARCH_BASE, filter, FIELDS[0] ) );
    }

    public float getSSSC( String CN ) throws DAOException {
        String filter = "(&(objectClass=group)(cn=" + CN + "))";
        return Float.parseFloat( _getAttributeValue( LDAP_SEARCH_BASE, filter, FIELDS[1] ) );
    }

    public boolean isGroup( String CN ) throws DAOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getMembers( String groupCN ) throws DAOException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

}
