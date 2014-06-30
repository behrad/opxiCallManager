package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.DirectoryCallManagerService;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all Agent management stuff in Opxi Call Manager. responsible
 * for creating, destroying, adding, removing and refreshing Agents in
 * system memory.
 * <p/>
 * Since Agent state changes seems not to be a concurrency issue
 * I've add no concurrency control here till now. e.g.
 * There's no need to define a critical section to add new agent
 * into AgentService, as there "NORMALY" will be one request to update an
 * agent global state (usually from the AgentMan).
 *
 * @author Jrad
 *         Date: Feb 20, 2006
 *         Time: 3:17:59 PM
 * @see com.basamadco.opxi.callmanager.pool.PoolService
 */
public class AgentService extends DirectoryCallManagerService {

    private static final Logger logger = Logger.getLogger( AgentService.class.getName() );


    private final Map<String, Agent> agentCache = new ConcurrentHashMap<String, Agent>();

//    private AgentDAO dao;


    public AgentService() {
        /*try {
            this.dao = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO();
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new RuntimeException( e.getMessage(), e );
        } catch ( DAOFactoryException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new RuntimeException( e.getMessage(), e );
        }*/
    }

    /**
     * Added to capsulate Agent access into QMS
     * This method may result in high contension on listAgents cache
     *
     * @param aor Agent's ID
     * @return Agent object representing this agent in opxi front manager
     * @throws AgentNotAvailableException if no agent is exists in the current system cache
     */
    public Agent getAgentByAOR( String aor ) throws AgentNotAvailableException {
        if ( agentCache.containsKey( aor ) ) {
            Agent agent = agentCache.get( aor );
//            logger.finest( "***** Fetching Agent with AOR = " + agent );
            return agent;
        }
        throw new AgentNotAvailableException( "No agent for name '" + aor + "' is available in AgentService" );
    }

    public boolean isAgentAvailableFor( UserAgent ua ) {
        try {
            getAgentByAOR( ua.getSipURIString() );
            return true;
        } catch ( AgentNotAvailableException e ) {
            return false;
        }
    }

    public Agent getAgentForUA( UserAgent ua ) throws AgentNotAvailableException {
        return getAgentByAOR( ua.getSipURIString() );
    }

    /**
     * Creates an Agent object for the specified ua and read's Agent's profile
     *
     * @param userAgent
     * @return the new Agent
     * @throws DAOException
     */
    private Agent loadAgent( UserAgent userAgent ) throws DAOException, AgentAlreadyLoadedException {
        if ( isAgentAvailableFor( userAgent ) ) {
            throw new AgentAlreadyLoadedException( userAgent.getSipURIString() );
        }
        try {
            logger.finest( "Load agent for: " + userAgent );
            Agent freshAgent = (Agent) BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().read( userAgent.getName() );
            freshAgent.setAOR( userAgent.getSipURIString() );
            freshAgent.setServiceFactory( getServiceFactory() );
            OpxiCMEntityProfile profile = BaseDAOFactory.getConfigStorageDAOFactory().getAgentProfileDAO( userAgent.getName() ).readProfile();
            freshAgent.assignProfile( profile );
            agentCache.put( freshAgent.getAOR(), freshAgent );
            freshAgent.setActivityLogId( getServiceFactory().getLogService().openAgentActivityLogger( userAgent ) );
            return freshAgent;
        } catch ( EntityNotExistsException ee ) {
            throw new DAOException( ee.getMessage(), ee );
        } catch ( DAOFactoryException ee ) {
            throw new DAOException( ee.getMessage(), ee );
        } catch ( ProfileException ee ) {
            throw new DAOException( ee.getMessage(), ee );
        }
    }

    public Agent getOrLoadAgent( UserAgent ua ) throws DAOException {
        Agent agent = null;
        try {
            agent = getAgentForUA( ua );
        } catch ( AgentNotAvailableException e ) {
            try {
                agent = loadAgent( ua );
            } catch ( AgentAlreadyLoadedException e1 ) {
                // this is not possible!
            }
        }
        return agent;
    }


    private Agent removeAgentFromCache( String agentAOR ) {
        if ( agentCache.containsKey( agentAOR ) ) {
            return agentCache.remove( agentAOR );
        } else {
            throw new IllegalStateException( "No Agent object for " + agentAOR + " in cache." );
        }
    }

    /**
     * Unloads the Agent with specified AOR, closes his/her activity log and then destroys it
     *
     * @param ua UserAgent object for which Agent Object should be removed from AgentService
     * @throws OpxiException
     */
    private void removeAgent( UserAgent ua, String cause ) throws OpxiException {
        unassignPool( ua ); // is nec. when an UNPublish but not valid request tries to remove the agent
        logger.finest( "Removing agent from agent service: " + ua );
        Agent agent = removeAgentFromCache( ua.getSipURIString() );
        getServiceFactory().getLogService().closeAgentActivityLogger( agent.getActivityLogId(), cause );
        agent.dispose();
    }


    public void notifyReadyForCall( UserAgent userAgent ) throws AgentNotAvailableException {
        // Agent should decide if is capale of this ready presence event or not!
        getAgentForUA( userAgent ).readyStateReceived();
//        for ( int i = 0; i < agent.getMaxOpenCalls(); i++ ) {
//            getServiceFactory().getQueueManagementService().serviceAgentIdleEvent( userAgent.getSipURIString() );
//        }
    }

    public void unassignPool( UserAgent userAgent ) throws OpxiException {
        logger.fine( "Unassign pools for: " + userAgent );
        Agent agent = getAgentForUA( userAgent );
        getServiceFactory().getPoolService().unassignPoolMemberships( agent );
    }

    public void assignToPool( UserAgent userAgent ) throws AgentNotAvailableException {
        logger.fine( "Assign pools for: " + userAgent );
        Agent agent = getAgentForUA( userAgent );
        getServiceFactory().getPoolService().assignPoolMemberships( agent );
    }

    /**
     * There should be some IP-based relation between REGISTERs and PUBLISHes
     * Notifies this service of UserAgent's registration events
     *
     * @param contact The registration Object
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
    public void onLogin( Registration contact ) throws OpxiException {
        UserAgent userAgent = contact.getUserAgent();
        logger.finest( "Login contact '" + userAgent + "'" );
        getOrLoadAgent( contact.getUserAgent() );
//        getServiceFactory().getLogService().getServiceActivityLogger().setOnlineAgents( agents().size() );
    }


    /**
     * Notifies this service of UserAgent's un-register events
     *
     * @param contact The registration that requested to un-register
     * @throws com.basamadco.opxi.callmanager.OpxiException
     *
     */
    public void onLogoff( Registration contact ) throws OpxiException {
        if ( isAgentAvailableFor( contact.getUserAgent() ) ) {
            logger.finest( "add Logoff Unregistration log '" + contact + "'" );
            Agent agent = getAgentForUA( contact.getUserAgent() );
            getServiceFactory().getLogService().getAgentActivityLogger( agent.getActivityLogId() ).addUnregistration( contact );
        }
    }

    public void unloadAgent( Registration contact ) throws OpxiException {
        if ( isAgentAvailableFor( contact.getUserAgent() ) ) {
            removeAgent( contact.getUserAgent(), contact.getComment() );
//            getServiceFactory().getLogService().getServiceActivityLogger().setOnlineAgents( agents().size() );
        } else {
            logger.warning( "No Agent available for '" + contact.getUserAgent().getSipURIString() + "' in AgentService" );
        }
    }

    public void logLoggedOffContact( Registration contact ) throws OpxiException {
        logger.finest( "log inactive Contact '" + contact + "'" );
        if ( isAgentAvailableFor( contact.getUserAgent() ) ) {
            Agent agent = getAgentForUA( contact.getUserAgent() );
            logger.warning( "The agent for this registration loaded before this registration." );
            getServiceFactory().getLogService().getAgentActivityLogger( agent.getActivityLogId() ).addUnregistration( contact );
        }
    }

    /**
     * Notifies Agent objects for profile updates
     *
     * @param profile an agent profile
     */
    public void agentProfileUpdateNotification( OpxiCMEntityProfile profile ) {
        try {
            String agentName = getDAOFactory().getAgentDAO().getCNForDN( profile.getOpxiCMEntityProfileChoice().getAgentProfile().getDN() );
            getAgentByAOR( SipUtil.toSipURIString( agentName ) ).assignProfile( profile );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        } catch ( AgentNotAvailableException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        } catch ( ProfileException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    /**
     * Returns a read-only list of all currently listAgents
     *
     * @return a list of Agent objects
     */
    public List agents() {
        Object[] content = agentCache.values().toArray();
        List ags = new ArrayList();
        for ( int i = 0; i < content.length; i++ ) {
            ags.add( content[i] );
        }
        return Collections.unmodifiableList( ags );
    }

    public List listObjects() {
        return agents();
    }

    public void dispose() {
        List all = agents();
        for ( int i = 0; i < all.size(); i++ ) {
            Agent agent = (Agent) all.get( i );
            agent.dispose();
        }
        agentCache.clear();
        logger.info( "AgentService destroyed successfully." );
    }

}