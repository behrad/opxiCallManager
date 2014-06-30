package com.basamadco.opxi.callmanager.entity.dao.webdav;

import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.pool.rules.Rule;
import com.basamadco.opxi.callmanager.pool.PoolTarget;

/**
 * @author Jrad
 *         Date: Oct 5, 2006
 *         Time: 2:31:48 PM
 */
public interface MatchingRuleDAO {

    public Rule getMatchingRule( PoolTarget pool ) throws DAOException;

    public void updateMatchingRule( String matchingRuleInXML ) throws DAOException;

}
