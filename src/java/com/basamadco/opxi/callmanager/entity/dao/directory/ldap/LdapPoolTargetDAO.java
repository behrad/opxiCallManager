package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.PoolTargetDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;
import com.basamadco.opxi.callmanager.pool.PoolTarget;


public class LdapPoolTargetDAO extends LdapDAO implements PoolTargetDAO {

    /*private static final String[] FIELDS = {
            "opxi-pssc", "opxi-sssc", "poolType", "displayName", "matchingRuleString"
    };*/

    private static final String[] FIELDS = {
            "displayName", "opxiHomeDirectoryURI", "telephoneNumber"
    };

    protected String[] fields() {
        return FIELDS;
    }


    protected String getMappedProperty( String attributeName ) {
        if( attributeName.equalsIgnoreCase( FIELDS[ 1 ] ) ) {
            return "homeURI";
        }
        return super.getMappedProperty( attributeName );
    }

    public String getDN( DirectoryEntity entity ) throws DAOException {
        throw new IllegalAccessError( "Don't use getDN!" );
//        if (!(entity instanceof PoolTarget )) {
//            throw new DAOException( "Specified dto was not an Group instance" );
//        }
//        PoolTarget group = (PoolTarget)entity;
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

    public LdapPoolTargetDAO( DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager ) {
        super( factory, connectionManager );
    }

    public DirectoryEntity read( String cn ) throws EntityNotExistsException, DAOException {
        PoolTarget t = new PoolTarget( cn );
        String filter = "(&(objectClass=group)(cn=" + cn + "))";
        read( filter, FIELDS, t, LDAP_SEARCH_BASE );
        return t;
    }

//    public void read( String cn, DirectoryEntity entity ) throws EntityNotExistsException, DAOException {
//        String filter = "(&(objectClass=group)(cn=" + cn + "))";
//        read( filter, FIELDS, entity, null );
//    }

    /**
     * @deprecated
     * @return
     * @throws DAOException
     */
    public String[] listAllByCNs() throws DAOException {
//		String filter = "(objectClass=group)";
//		List groups = new ArrayList();
//		List group_names = (List)search( filter, new String[] {"name"} );
//		for( int i = 0; i < group_names.size(); i++ ) {
//			String group_name = (String)group_names.get( i );
//			groups.add( new Group( group_name ) );
//		}
        throw new DAOException( "This method is deprecated..." );
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

//    public float getPSSC( String CN ) throws DAOException {
//        String filter = "(&(objectClass=group)(cn=" + CN + "))";
//        return Float.parseFloat( _getAttributeValue( LDAP_SEARCH_BASE, filter, FIELDS[0] ) );
//    }
//
//    public float getSSSC( String CN ) throws DAOException {
//        String filter = "(&(objectClass=group)(cn=" + CN + "))";
//        return Float.parseFloat( _getAttributeValue( LDAP_SEARCH_BASE, filter, FIELDS[1] ) );
//    }

    public boolean isGroup( String CN ) throws DAOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[] getMembers( String groupCN ) throws DAOException {
        String[] memberDNs = getAttributeValues( groupCN, "member" );
        String[] memberCNs = new String[ memberDNs.length ];
        for (int i = 0; i < memberDNs.length; i++) {
            memberCNs[ i ] = getCNForDN( memberDNs[i] );
        }
        return memberCNs;
    }


    public String getHomeURI( String poolName ) throws DAOException {
        String homeUri = getAttributeValue( poolName, FIELDS[1] );
        if( homeUri == null ) {
            throw new DAOException( "Couldn't read pool profile: home directory not set for '" + poolName + "'" );
        }
        return homeUri;
    }
}