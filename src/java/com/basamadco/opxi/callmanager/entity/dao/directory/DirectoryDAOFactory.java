package com.basamadco.opxi.callmanager.entity.dao.directory;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;


/**
 * @author Jrad
 *         Date: Feb 5, 2006
 *         Time: 11:00:27 AM
 */
public abstract class DirectoryDAOFactory extends BaseDAOFactory {

//    public abstract DirectoryDAO getGenericDAO() throws DAOException;

    public abstract PoolTargetDAO getPoolTargetDAO() throws DAOException;

    public abstract QueueTargetDAO getQueueTargetDAO() throws DAOException;

    public abstract AgentDAO getAgentDAO() throws DAOException;

    public abstract TrunkDAO getTrunkDAO() throws DAOException;

    public abstract CallTargetDAO getCallTargetDAO() throws DAOException;

}
