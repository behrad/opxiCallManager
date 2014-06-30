package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.DirectoryEntity;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAO;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.novell.ldap.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * An DirectoryDAO implementation common for all LdapDAOs in opxiCallManager
 *
 * @author Jrad
 */
public abstract class LdapDAO implements DirectoryDAO {


    private static final Logger logger = Logger.getLogger(DirectoryDAO.class.getName());

    /**
     * Attribute name under which user passwords are stored in DIRECTORY server
     */
    protected static final String LDAP_USER_PASS_ATTRIBUTE = PropertyUtil.getProperty("opxi.ldap.attributes.passwd");

    /**
     * Attribute name under which user phone numbers are stored in DIRECTORY server
     */
    protected static final String LDAP_USER_PHONE_ATTRIBUTE = PropertyUtil.getProperty("opxi.ldap.attributes.phone");

    /**
     * Attribute name to be returned as a user's sip username
     */
    protected static final String LDAP_USER_SIP_ATTRIBUTE = PropertyUtil.getProperty("opxi.ldap.attributes.sip.username");


    protected static final String LDAP_OU = PropertyUtil.getProperty("opxi.ldap.organizationUnit");

    /**
     * Search base attribute name which OpxiCallManager searches the DIRECTORY server for
     */
    protected static final String LDAP_SEARCH_BASE = buildSearchBase();

    /**
     * DIRECTORY server administrator login name
     */
    private static final String LOGIN_USERNAME = PropertyUtil.getProperty("opxi.ldap.admin.username");

    /**
     * DIRECTORY server administrator login password
     */
    private static final String LOGIN_PASS = PropertyUtil.getProperty("opxi.ldap.admin.passwd");


    public static final String AGENT_TREE = PropertyUtil.getProperty("opxi.ldap.agent");

    public static final String SKILL_TREE = PropertyUtil.getProperty("opxi.ldap.skill");

    public static final String GROUP_TREE = PropertyUtil.getProperty("opxi.ldap.group");

    public static final String SERVICE_TREE = PropertyUtil.getProperty("opxi.ldap.service");

    public static final String TRUNK_TREE = PropertyUtil.getProperty("opxi.ldap.trunk");


    protected static final String AGENT_SEARCH_BASE = "CN=" + AGENT_TREE + "," + LDAP_SEARCH_BASE;

    protected static final String SKILL_SEARCH_BASE = "CN=" + SKILL_TREE + "," + LDAP_SEARCH_BASE;

    protected static final String GROUP_SEARCH_BASE = "CN=" + GROUP_TREE + "," + LDAP_SEARCH_BASE;

    protected static final String SERVICE_SEARCH_BASE = "CN=" + SERVICE_TREE + "," + LDAP_SEARCH_BASE;

    protected static final String TRUNK_SEARCH_BASE = "CN=" + TRUNK_TREE + "," + LDAP_SEARCH_BASE;


    protected DirectoryConnectionManager connectionManager = null;

    protected DirectoryDAOFactory factory;

    protected abstract String[] fields();

    public String getSearchBase() {
        return LDAP_SEARCH_BASE;
    }

    public LdapDAO(DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager) {
        this.factory = factory;
        this.connectionManager = connectionManager;
    }

    protected LDAPConnection getConnection() throws DAOException {
        return connectionManager.getConnection(getAdminDN(LOGIN_USERNAME), LOGIN_PASS);
        /*LDAPConnection lc = new LDAPConnection();
        try {
            lc.connect( configuration.getHost(), configuration.getPort() );
//            logger.finest( "Binding to ldap server with DN='" + getAdminDN( configuration.getUsername() 
//                    + "' and PASS='" + configuration.getPassword().getBytes() + "'" ) );
            lc.bind( configuration.getVersion(),  ),
                    configuration.getPassword().getBytes() );
            return lc;
        } catch( LDAPException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( "Couldn't connect to ldap: " + e.getMessage() );
        }*/
    }

    private static String buildSearchBase() {
        String domain = OpxiToolBox.getLocalDomain();
        String[] subDomains = domain.split("\\.");
        StringBuffer ldapSuffix = new StringBuffer("OU=" + LDAP_OU);
        for (int i = 0; i < subDomains.length; i++) {
            String subDomain = subDomains[i];
            ldapSuffix.append("," + "DC=" + subDomain);
        }
//        logger.finest("Ldap search base is '" + ldapSuffix + "'");
        return ldapSuffix.toString();
    }

    private String getAdminDN(String userCN) throws DAOException {
        return new StringBuffer()
                .append("CN=")
                .append(userCN)
                .append(",CN=")
                .append(AGENT_TREE)
                .append(',')
                .append(LDAP_SEARCH_BASE)
                .toString();
    }

    public String getCNForDN(String dN) {
        if (dN != null) {
            int start = dN.indexOf("=");
            int end = dN.indexOf(",");

            if (end == -1) {
                end = dN.length();
            }

            return dN.substring(start + 1, end);
        } else {
            return null;
        }
    }

    public String[] getCNForDN(String[] dNs) {
        for (int i = 0; i < dNs.length; i++)
            dNs[i] = getCNForDN(dNs[i]);
        return dNs;
    }

    protected void populate(LDAPEntry entry, DirectoryEntity entity) throws DAOException {
//        System.out.println( "POPULATE: " + entry.getDN() );
        entity.setDN(entry.getDN());
        entity.setCN(getCNForDN(entry.getDN()));
//        checkEntityType( entity );
        Iterator attrs = entry.getAttributeSet().iterator();
        while (attrs.hasNext()) {
            LDAPAttribute attr = (LDAPAttribute) attrs.next();
//            System.out.println( "Set Attribute " + attr.getName() + "=" + attr.getStringValueArray() + " in " + entity );
            setField(getMappedProperty(attr.getName()), attr.getStringValueArray(), entity);
        }
    }


    private void setField(String attrName, String[] attrValues, Object object) throws DAOException {
        String methodName = "set" + Character.toUpperCase(attrName.charAt(0)) + attrName.substring(1);
        try {
//            logger.debug( "+++++++++ Attribute Value Cardinality: " + attrValues.length );
            if (attrValues.length == 1) {
                object.getClass().getMethod(methodName, new Class[]{String.class}).invoke(object, new Object[]{attrValues[0]});
//                logger.debug( object.getClass() + "." + methodName + "( " + attrValues[0] + " )" );
            } else {
                object.getClass().getMethod(methodName, new Class[]{String[].class}).invoke(object, new Object[]{attrValues});
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage(), e);
        }
    }

    protected DirectoryEntity searchByPhoneNumber(String phoneNumber) throws EntityNotExistsException, DAOException {
        String[] fields = new String[]{"cn"};
        String filter = "(telephoneNumber=" + phoneNumber + ")";
        LDAPConnection connection = null;
        LDAPSearchResults resultset;
        try {
            connection = getConnection();
            resultset = connection.search(LDAP_SEARCH_BASE, LDAPConnection.SCOPE_SUB, filter, fields, false);
            while (resultset.hasMore()) {
                try {
                    LDAPEntry entry = resultset.next();
                    DirectoryEntity dr = new DirectoryEntity();
                    dr.setDN(entry.getDN());
                    dr.setCN(getCNForDN(entry.getDN()));
                    return dr;
                } catch (LDAPException e) {
                    throw new DAOException(e.getMessage(), e);
                }
            }
            throw new EntityNotExistsException(phoneNumber);
        } catch (LDAPException e) {
            throw new DAOException("Couldn't read from ldap " + e);
        } finally {
            if (connection != null) {
                connectionManager.free(connection);
            }
        }
    }

    protected DirectoryEntity searchByPattern(String telNumber) throws EntityNotExistsException, DAOException {
        String[] fields = new String[]{"cn", "keywords"};
        String filter = "(keywords=dialPattern:*)";
        LDAPConnection connection = null;
        LDAPSearchResults resultset;
//        logger.finest("searching trunk for dialed number : " + telNumber);
        try {
            connection = getConnection();
            resultset = connection.search(LDAP_SEARCH_BASE, LDAPConnection.SCOPE_SUB, filter, fields, false);

            while (resultset.hasMore()) {
                try {
                    LDAPEntry entry = resultset.next();
                    DirectoryEntity dr = new DirectoryEntity();

                    String dialPattern = null;
                    LDAPAttributeSet attrSet = entry.getAttributeSet();
                    java.util.Iterator attrIterator = attrSet.iterator();

                    while (attrIterator.hasNext()) {
                        com.novell.ldap.LDAPAttribute attr = (com.novell.ldap.LDAPAttribute) attrIterator.next();
                        if (!attr.getName().equalsIgnoreCase("keywords")) continue;
                        String[] attrValues = attr.getStringValueArray();
                        for (int i = 0; i < attrValues.length; i++) {
                            dialPattern = attrValues[i];
                            if (dialPattern == null) continue;
                            String[] arr = dialPattern.split("dialPattern:");
                            if (arr.length < 2) continue;
                            dialPattern = arr[1].trim();
//                            dialPattern = dialPattern.replaceAll("\\*", "\\\\d*").replaceAll("\\.", "\\\\d");
                            if (Pattern.compile(dialPattern).matcher(telNumber).matches()) {
                                logger.finest("Found a trunk: " + dialPattern);
                                dr.setDN(entry.getDN());
                                dr.setCN(getCNForDN(entry.getDN()));
                                return dr;
                            }

                        }
                    }
                } catch (LDAPException e) {
                    throw new DAOException(e.getMessage(), e);
                }
            }
//            logger.finer("No trunk found for Dialed Number" + telNumber + " .");
            throw new EntityNotExistsException(telNumber);
        } catch (LDAPException e) {
            throw new DAOException("Couldn't read from ldap " + e);
        } finally {
            if (connection != null) {
                connectionManager.free(connection);
            }
        }
    }

    public DirectoryEntity read(String cn, String searchBase) {
        return null;
    }

    public DirectoryEntity read(String cn) throws EntityNotExistsException, DAOException {
        String dn = getAttributeValue(cn, "DN");
        if (dn == null) {
            throw new EntityNotExistsException(cn);
        }
        DirectoryEntity entity = new DirectoryEntity();
        entity.setCN(cn);
        entity.setDN(dn);
        return entity;
    }

    protected void read(String filter, String[] fields, DirectoryEntity entity, String overrideSearchBase) throws DAOException, EntityNotExistsException {
        if (overrideSearchBase == null) {
            overrideSearchBase = getSearchBase();
        }

        if (fields == null)
            fields = new String[]{"cn"};

        LDAPConnection connection = null;
        LDAPSearchResults resultset;
        try {
            connection = getConnection();
//            System.out.println( "Search: " + filter + ", " + fields[0] + ", " + overrideSearchBase  );
            resultset = connection.search(overrideSearchBase, LDAPConnection.SCOPE_SUB, filter, fields, false);
            while (resultset.hasMore()) {
//                System.out.println( "Result: " + resultset.getCount() );
                try {
                    populate(resultset.next(), entity);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new DAOException(e.getMessage(), e);
                }
            }
            throw new EntityNotExistsException(filter);
        } catch (LDAPException e) {
            e.printStackTrace();
            throw new DAOException("Couldn't read from ldap " + e);
        } finally {
            if (connection != null) {
                connectionManager.free(connection);
            }
        }
    }


    public String[] getAttributeValues(String CN, String attr_name) throws DAOException {
        String searchFilter = "(cn=" + CN + ")";
        return _getAttributeValues(searchFilter, attr_name);
    }

    public String getAttributeValue(String CN, String attr_name) throws DAOException {
        String filter = "(cn=" + CN + ")";
        return _getAttributeValue(filter, attr_name);
    }

    protected String _getAttributeValue(String searchFilter, String attr_name) throws DAOException {
        return _getAttributeValue(getSearchBase(), searchFilter, attr_name);
    }

    /**
     * Retreives an attribute value from the DIRECTORY server for specified
     * user principal name and attribute name.
     *
     * @param searchFilter filter
     * @param attr_name    Attribute name to be read
     * @return String value of the attribute
     * @throws DAOException
     */
    protected String _getAttributeValue(String searchBase, String searchFilter, String attr_name) throws DAOException {
        String value = null;

        // logger.finest( "Getting an available connection..." );
        LDAPConnection connection = getConnection();
        try {
//            logger.finer("Searching ldap: searchBase='" + searchBase + "', filter='" + searchFilter
//                    + "' for attribute " + attr_name);
            LDAPSearchResults searchResults = connection.search(
                    searchBase, LDAPConnection.SCOPE_SUB, searchFilter, new String[]{attr_name}, false
            );
            while (searchResults.hasMore()) {
                LDAPEntry nextEntry;
                try {
                    nextEntry = searchResults.next();
                }
                catch (LDAPException e) {
                    continue;
                }
                if (attr_name.equalsIgnoreCase("dn")) {
                    value = nextEntry.getDN();
                    break;
                }
                LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
                Iterator allAttributes = attributeSet.iterator();
                while (allAttributes.hasNext()) {
                    LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
                    value = attribute.getStringValue();
                }
            }
//            connection.disconnect();
//            connectionManager.free( connection );
            return value;
        } catch (LDAPException e) {
            throw new DAOException("Error searching ldap", e);
        } finally {
            if (connection != null) {
                connectionManager.free(connection);
            }
        }
    }

    protected String[] _getAttributeValues(String searchFilter, String attr_name) throws DAOException {
        return _getAttributeValues(getSearchBase(), searchFilter, attr_name);
    }

    protected String[] _getAttributeValues(String searchBase, String searchFilter, String attr_name) throws DAOException {
        String[] values = new String[0];
        LDAPConnection connection = getConnection();
        try {
            LDAPSearchResults searchResults = connection.search(
                    searchBase, LDAPConnection.SCOPE_SUB, searchFilter, new String[]{attr_name}, false
            );
            while (searchResults.hasMore()) {
                LDAPEntry nextEntry;
                try {
                    nextEntry = searchResults.next();
//                    logger.debug( "Search result: " + nextEntry.getDN() );
                }
                catch (LDAPException e) {
                    continue;
                }
                LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
                Iterator allAttributes = attributeSet.iterator();
                while (allAttributes.hasNext()) {
                    LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
                    values = attribute.getStringValueArray();
                }
            }
//            connection.disconnect();
            return values;
        } catch (LDAPException e) {
            e.printStackTrace();
            throw new DAOException("Error searching ldap", e);
        } finally {
            if (connection != null) {
                connectionManager.free(connection);
            }
        }
    }

    public BaseDAOFactory getDAOFactory() {
        return factory;
    }

    public void removeAttribute(String CN, String attr_name) throws DAOException {
        LDAPConnection connection = getConnection();
        String searchFilter = "(cn=" + CN + ")";
        try {
//            logger.finer("Searching ldap: searchBase='" + getSearchBase() + "', filter='" + searchFilter
//                    + "' for attribute " + attr_name);
            LDAPSearchResults searchResults = connection.search(
                    getSearchBase(), LDAPConnection.SCOPE_SUB, searchFilter, new String[]{attr_name}, false
            );
            while (searchResults.hasMore()) {
                LDAPEntry nextEntry;
                try {
                    nextEntry = searchResults.next();
                }
                catch (LDAPException e) {
                    continue;
                }

                LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();
                Iterator allAttributes = attributeSet.iterator();
                while (allAttributes.hasNext()) {
                    LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
                    attribute.removeValue(attribute.getStringValue());

                }
            }
//            connection.disconnect();
//            connectionManager.free( connection );

        } catch (LDAPException e) {
            throw new DAOException("Error searching ldap", e);
        } finally {
            if (connection != null) {
                connectionManager.free(connection);
            }
        }
    }


    public void updateAttribute(String cn, String attrName, String attrValue) throws DAOException {
        LDAPConnection conn = null;
        try {
            conn = getConnection();
            String dn = "CN=" + cn + ", " + getSearchBase();
            LDAPAttribute attr = new LDAPAttribute(attrName);
            LDAPModification[] mods = new LDAPModification[1];
//        mods[ 0 ] = new LDAPModification( LDAPModification.DELETE, attr );
            attr.addValue(attrValue);
            mods[0] = new LDAPModification(LDAPModification.REPLACE, attr);
            conn.modify(dn, mods);
//            conn.disconnect();
        } catch (LDAPException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                connectionManager.free(conn);
            }
        }
    }

    public void updateAttribute(String cn, String attrName, String[] attrValues) throws DAOException {
        LDAPConnection conn = null;
        try {
            conn = getConnection();
            String dn = "CN=" + cn + ", " + getSearchBase();
            LDAPAttribute attr = new LDAPAttribute(attrName);
            LDAPModification[] mods = new LDAPModification[1];
//        mods[ 0 ] = new LDAPModification( LDAPModification.DELETE, attr );
            for (int i = 0; i < attrValues.length; i++) {
                String value = attrValues[i];
                attr.addValue(value);
            }
            mods[0] = new LDAPModification(LDAPModification.REPLACE, attr);
            conn.modify(dn, mods);
//            conn.disconnect();
        } catch (LDAPException e) {
            throw new DAOException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                connectionManager.free(conn);
            }
        }
    }

//    public String getMatchingRule( String cn ) throws DAOException {
//        String filter = "(cn=" + cn + ")";
//        return _getAttributeValue( LDAP_SEARCH_BASE, filter, "matchingRuleString" );
//    }

    protected String getMappedProperty(String attributeName) {
        if (attributeName.equalsIgnoreCase("displayName")) {
            return "name";
        }
        return attributeName;
    }

    public String getCNForName(String name) throws DAOException {
        String filter = "(displayName=" + name + ")";
        return _getAttributeValue(LDAP_SEARCH_BASE, filter, "cn");
    }

}