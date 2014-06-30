package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;

/**
 * Created by IntelliJ IDEA.
 * User: AM
 * Date: Dec 18, 2007
 * Time: 1:51:05 AM
 */
public class LdapSkillDAO extends LdapCallTargetDAO {

    public LdapSkillDAO(DirectoryDAOFactory factory, DirectoryConnectionManager connectionManager, CallTargetCacheManager callTargetCache) {
        super(factory, connectionManager, callTargetCache);
    }

    public String getSearchBase() {
        return SKILL_SEARCH_BASE;
//        String subcontext = new StringBuffer()
//                .append("CN=")
//                .append(SKILL_TREE)
//                .append(',')
//                .append(LDAP_SEARCH_BASE).toString();
//        return subcontext;
    }

}
