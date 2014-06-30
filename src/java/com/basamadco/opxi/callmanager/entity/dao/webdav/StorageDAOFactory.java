package com.basamadco.opxi.callmanager.entity.dao.webdav;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.activitylog.schema.OpxiActivityLog;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 11:37:49 AM
 */
public abstract class StorageDAOFactory extends BaseDAOFactory {

    public static final Storage STORAGE_TYPE = Storage.valueOf(
            PropertyUtil.getProperty( "opxi.callmanager.storage.type" ).toLowerCase()
    );

    public abstract AgentProfileDAO getAgentProfileDAO( String agentName ) throws DAOException;

    public abstract PoolTargetProfileDAO getPoolTargetProfileDAO( String poolName ) throws DAOException;

//    public abstract MatchingRuleDAO getMatchingRuleDAO( String skillName ) throws DAOException;

    public abstract LogReportDAO getLogReportDAO( String userName, OpxiActivityLog logVO ) throws DAOException;

    public abstract LogReportDAO getTempLogReportDAO( OpxiActivityLog logVO ) throws DAOException;

}