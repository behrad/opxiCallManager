package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.*;
import com.basamadco.opxi.activitylog.schema.OpxiActivityLog;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 11:40:22 AM
 */
public class ExchangeDAOFactory extends StorageDAOFactory {

    private static final Logger logger = Logger.getLogger( ExchangeDAOFactory.class.getName() );


    public PoolTargetProfileDAO getPoolTargetProfileDAO( String poolName ) throws DAOException {
        try {
            String homeUri = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().getHomeURI( poolName );
            return new ExchangePoolTargetProfileDAO( this, homeUri );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }

    public AgentProfileDAO getAgentProfileDAO( String agentName ) throws DAOException {
        try {
            String homeUri = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getHomeURI( agentName );
            return new ExchangeAgentProfileDAO( this, agentName, homeUri );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }

    public MatchingRuleDAO getMatchingRuleDAO( String skillName ) throws DAOException {
        try {
            String uri = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().getHomeURI( skillName );
            return new ExchangeMatchingRuleDAO( this, uri );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }


    public LogReportDAO getLogReportDAO( String userName, OpxiActivityLog logVO ) throws DAOException {
        try {
            String uri = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getHomeURI( userName );
            return new ExchangeLogReportDAO( this, userName, uri, logVO );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }

    public LogReportDAO getTempLogReportDAO( OpxiActivityLog logVO ) throws DAOException {
        try {
            String uri = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getHomeURI( ExchangeDAO.EXCHANGE_USERNAME );
            return new ExchangeTempLogReportDAO( this, uri, logVO );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }

}
