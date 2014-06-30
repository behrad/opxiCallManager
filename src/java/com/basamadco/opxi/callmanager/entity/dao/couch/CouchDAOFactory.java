package com.basamadco.opxi.callmanager.entity.dao.couch;

import com.basamadco.opxi.callmanager.entity.dao.webdav.StorageDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.AgentProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.webdav.PoolTargetProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.webdav.LogReportDAO;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.couch.CouchAgentProfileDAO;
import com.basamadco.opxi.activitylog.schema.OpxiActivityLog;

/**
 * @author Jrad
 *         Date: May 7, 2010
 *         Time: 2:04:26 PM
 */
public class CouchDAOFactory extends StorageDAOFactory {

//    public CouchDAO getCouchDAO() throws DAOException {
//        return new CouchDAO( this );
//    }

    public AgentProfileDAO getAgentProfileDAO( String agentName ) throws DAOException {
        return new CouchAgentProfileDAO( this, agentName );
    }

    public PoolTargetProfileDAO getPoolTargetProfileDAO( String poolName ) throws DAOException {
        return new com.basamadco.opxi.callmanager.entity.dao.couch.CouchPoolTargetProfileDAO( this, poolName );
    }

    public LogReportDAO getLogReportDAO( String userName, OpxiActivityLog logVO ) throws DAOException {
        return new com.basamadco.opxi.callmanager.entity.dao.couch.CouchLogReportDAO( this, userName, logVO );
    }

    public LogReportDAO getTempLogReportDAO( OpxiActivityLog logVO ) throws DAOException {
        throw new DAOException( "Temporary logs not supported on CouchDB storage" );
    }
}
