package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.directory.AgentDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;
import com.basamadco.opxi.callmanager.pool.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LdapAgentDAO extends LdapDAO implements AgentDAO {

    protected static final Logger logger = Logger.getLogger(LdapAgentDAO.class.getName());

    private static final String[] FIELDS = {
//            "skillEfficiency", "maxOpenCalls", "telephoneNumber", "soundUrl"
            "telephoneNumber", "opxiHomeDirectoryURI"
    };

    public LdapAgentDAO(DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager) {
        super(factory, connectionManager);
    }

    protected String[] fields() {
        return FIELDS;
    }

    protected String getMappedProperty(String attributeName) {
        if (attributeName.equalsIgnoreCase(FIELDS[1])) {
            return "homeURI";
        }
        return super.getMappedProperty(attributeName);
    }

    public String getDN(DirectoryEntity entity) throws DAOException {
        if (!(entity instanceof Agent)) {
            throw new DAOException("Specified entity was not an Agent instance");
        }

        Agent agent = (Agent) entity;
        return getDN(agent.getName());
    }

    public String getDN(String CN) throws DAOException {
        return new StringBuffer()
                .append("CN=")
                .append(CN)
                .append(",CN=")
                .append(AGENT_TREE)
                .append(',')
                .append(LDAP_SEARCH_BASE)
                .toString();
    }

    public String getSearchBase() {
        String subcontext = new StringBuffer()
                .append("CN=")
                .append(AGENT_TREE)
                .append(',')
                .append(LDAP_SEARCH_BASE).toString();
        return subcontext;
    }

//    public void addMembership( String agentCN, String groupCN ) throws DAOException {
//        try {
//            LDAPConnection conn = getConnection();
//            String dn = getCN( agentCN );
//            String[] groups = getAttributeValues( agentCN, "memberOf" );
//            LDAPAttribute attr = new LDAPAttribute( "memberOf" );
//            LDAPModification[] mods = new LDAPModification[ 1 ];
//            for (int i = 0; i < groups.length; i++) {
//                String group = groups[i];
//                attr.addValue( group );
//            }
//            attr.addValue( ((DirectoryDAOFactory)getDAOFactory()).getGroupDAO().getCN( groupCN ) );
//            mods[ 0 ] = new LDAPModification( LDAPModification.REPLACE, attr );
//            conn.modify( dn, mods );
//            conn.disconnect();
//        } catch ( LDAPException e ) {
//            throw new DAOException( e.getMessage(), e );
//        }
//    }


    public boolean isMemberOf(String agentCN, String groupCN) throws DAOException {

//        String subcontext = new StringBuffer()
//        .append("CN=")
//        .append( AGENT )
//        .append(',')
//        .append( LDAP_SEARCH_BASE ).toString();
//		String filter = "(objectClass=user)";
//		List listAgents = new ArrayList();
//		List agent_names = (List)search( subcontext, filter, "cn" );
//		for( int i = 0; i < agent_names.size(); i++ ) {
//			String agent_name = (String)agent_names.get( i );
//			listAgents.add( Agent.getInstanceForName( agent_name ) );
//		}
//		return listAgents;
        return false;
    }

    public DirectoryEntity read(String cn) throws EntityNotExistsException, DAOException {
        Agent entity = new Agent();
        String filter = "(&(objectClass=user)(cn=" + cn + "))";
        read(filter, FIELDS, entity, getSearchBase());
        entity.setPoolMemberships(getPoolMemberships(cn));
        return entity;
    }

    public void read(String cn, DirectoryEntity entity) throws EntityNotExistsException, DAOException {
        String filter = "(&(objectClass=user)(cn=" + cn + "))";
        read(filter, FIELDS, entity, null);
    }

//	public List getAllAgents() throws DAOException {
//		String subcontext = new StringBuffer()
//        .append("CN=")
//        .append( AGENT )
//        .append(',')
//        .append( LDAP_SEARCH_BASE ).toString();
//		String filter = "(objectClass=user)";
//		List listAgents = new ArrayList();
//		List agent_names = (List)search( subcontext, filter, "cn" );
//		for( int i = 0; i < agent_names.size(); i++ ) {
//			String agent_name = (String)agent_names.get( i );
//			listAgents.add( Agent.getInstanceForName( agent_name ) );
//		}
//		return listAgents;
//	}
//
//	public List getAgentsInGroup( Group group ) throws DAOException {
//		String dn = getDAOFactory().getGroupDAO().getCN( group );
//		String filter = "(&(objectClass=user)(memberOf=" + dn + "))";
//        List listAgents = new ArrayList();
//        List strings = (List)search( null, filter, null );
//        for( int i = 0; i < strings.size(); i++ ) {
//			String agent_name = (String)strings.get( i );
//			listAgents.add( Agent.getInstanceForName( agent_name ) );
//		}
//        return listAgents;
//	}

    /**
     * Does the tricky user password retreival from the
     * OpxiCallManager configured DIRECTORY server.
     *
     * @param username DIRECTORY User name
     * @return User's password
     * @throws DAOException If any error occurs while binding to DIRECTORY server.
     */
    public String getUserPassword(String username) throws DAOException {
//        String searchFilter = "(&(objectClass=user)("+LDAP_USER_SIP_ATTRIBUTE+"=" + username + "))";
//        logger.finest("Getting " + username + "'s " + LDAP_USER_PASS_ATTRIBUTE);
        return getAttributeValue(username, LDAP_USER_PASS_ATTRIBUTE);
    }

    public String getManagerNameFor(String agentCN) throws DAOException {
        String managerDN = getAttributeValue(agentCN, "manager");
//        System.out.println( "+++ " + managerDN );
        return getCNForDN(managerDN);
    }


    public List getPoolMemberships(String agentCN) throws DAOException {
        String searchFilter = "(&(objectClass=user)(cn=" + agentCN + "))";
        List skills = new ArrayList();
        String[] values = _getAttributeValues(searchFilter, "memberOf");
        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (containsIgnoreCase(value, GROUP_TREE) || containsIgnoreCase(value, SKILL_TREE)) {
                skills.add(getCNForDN(value));
            }
        }
        return skills;
    }

    private boolean containsIgnoreCase(String src, String dest) {
        src = src.toLowerCase();
        dest = dest.toLowerCase();
        return src.indexOf(dest) > 0;
    }

    /**
     * Retreives the username information from DIRECTORY server for specified phone number
     *
     * @param phoneNumber User's phone number
     * @return User's DIRECTORY username
     * @throws DAOException
     */
    public String getUsernameFor(String phoneNumber) throws DAOException {
        String searchFilter = "(&(objectClass=user)(" + LDAP_USER_PHONE_ATTRIBUTE + "=" + phoneNumber + "))";
        return getAttributeValue(searchFilter, LDAP_USER_SIP_ATTRIBUTE);
    }

    public String getAttributeValue(String CN, String attr_name) throws DAOException {
        String searchFilter = "(&(objectClass=user)(cn=" + CN + "))";
        return _getAttributeValue(searchFilter, attr_name);
    }

    public String[] getAttributeValues(String CN, String attr_name) throws DAOException {
        String searchFilter = "(&(objectClass=user)(cn=" + CN + "))";

        // logger.finest( "Returning searchFilter : " + searchFilter);
        return _getAttributeValues(searchFilter, attr_name);
    }

    public String[] listAllByCNs() throws DAOException {
//        String filter = "(objectClass=user)";
//        LDAPSearchResults agent_names = search( filter, new String[] { "cn" } );
//        String[] listAgents = new String[ agent_names.getCount() ];
//        for( int i = 0; i < agent_names.getCount(); i++ ) {
////            listAgents[ i ] = (String)agent_names.get( i );
////            listAgents.add( Agent.getInstanceForName( agent_name ) );
//        }
//        return listAgents;
        return null;
    }


    public String getHomeURI(String agentCN) throws DAOException {
        String homeUri = getAttributeValue(agentCN, FIELDS[1]);
        if (homeUri == null) {
            throw new DAOException("Couldn't read agent profile: home directory not set for '" + agentCN + "'");
        }
        return homeUri;
    }
}