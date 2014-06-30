package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 18, 2007
 * Time: 2:09:39 AM
 */
public class LdapGroupDAO extends LdapCallTargetDAO {

    public LdapGroupDAO(DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager, CallTargetCacheManager callTargetCache) {
        super(factory, connectionManager, callTargetCache);
    }

    public String getSearchBase() {
        return GROUP_SEARCH_BASE;
//        String subcontext = new StringBuffer()
//                .append("CN=")
//                .append(GROUP_TREE)
//                .append(',')
//                .append(LDAP_SEARCH_BASE).toString();
//        return subcontext;
    }

}
