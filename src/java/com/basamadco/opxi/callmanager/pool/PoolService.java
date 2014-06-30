package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.DirectoryCallManagerService;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.UserNotAvailableException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.logging.ActivityLogNotExistsException;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.queue.Queue;
import com.basamadco.opxi.callmanager.queue.QueueManagerException;
import com.basamadco.opxi.callmanager.queue.QueueNotExistsException;
import com.basamadco.opxi.callmanager.sip.util.SIPConstants;
import com.basamadco.opxi.callmanager.sip.presence.NoActivePublishContextException;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OPXi Call Manager Pool service is responsible for managing pools of
 * Agents in system and tightly uses AgentService services.
 *
 * @author Jrad
 *         Date: Apr 6, 2006
 *         Time: 1:47:25 PM
 * @see com.basamadco.opxi.callmanager.pool.AgentService
 */
public class PoolService extends DirectoryCallManagerService {

    private static final Logger logger = Logger.getLogger( PoolService.class.getName() );

    private final Map<String, AgentPool> pools = new ConcurrentHashMap<String, AgentPool>();


    public PoolService() {
        super();
    }


    public void setServiceFactory( ServiceFactory factory ) {
        super.setServiceFactory( factory );
    }

    //
    /**
     * Creates an AgentPool object for specified pool name:
     * 1. Reads the PoolTarget from directory server
     * 2. Loads the pool profile from WebDAV server
     * 3. Instantiates AgentPool object with the specified
     * pool class type in the profile
     *
     * @param id pool name
     * @return the propert AgentPool object
     * @throws PoolInitializationException
     */
    private AgentPool createPool( String id ) throws PoolInitializationException {
        try {
            PoolTarget target = (PoolTarget) getDAOFactory().getPoolTargetDAO().read( id );
            target.setServiceFactory( getServiceFactory() );
            target.assignProfile(
                    BaseDAOFactory.getConfigStorageDAOFactory().getPoolTargetProfileDAO( id ).readProfile()
            );
            Class poolType = Class.forName( target.getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getType() );
            Class[] TargetParam = new Class[]{PoolTarget.class};
            Object poolObject = poolType.getConstructor( TargetParam ).newInstance( target );
            // TODO to avoid the name conflict between listAgents and groups (added the prefix: Skill)
            UserAgent poolUA = new UserAgent( getServiceFactory().getSipService().createSipURIForName( id + "Skill" ) );
            Presence poolStatus = new Presence( poolUA );
            poolStatus.setBasic( SIPConstants.BASIC_STATUS_OPEN );
            poolStatus.setNote( "Created" );
            poolStatus.setActivity( Presence.ACTIVITY_UNKNOWN );
            getServiceFactory().getPresenceService().servicePresence( poolStatus );
            AgentPool ap = (AgentPool) poolObject;
//            ap.assign
            return ap;
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, "Cannot create pool '" + id + "'", e );
            throw new PoolInitializationException( e.getMessage(), e );
        }
    }

    /**
     * Returns the AgentPool object available in this service for specified id
     *
     * @param id pool Id
     * @return AgentPool object for id
     * @throws NoPoolAvailableException if no pool with the input id is loaded in
     *                                  system's memory.
     */
    public AgentPool getPool( String id ) throws NoPoolAvailableException {
        Object o = pools.get( id );
        if ( o != null ) {
//            logger.finest( "Fetched pool " + o );
            return (AgentPool) o;
        }
        throw new NoPoolAvailableException( "No Agent Pool available for " + id );
    }

    public AgentPool getOrLoadPool( String id ) throws PoolInitializationException {
        AgentPool pool;
        try {
            pool = getPool( id );
        } catch ( NoPoolAvailableException e ) {
            pool = createPool( id );
        }
        return pool;
    }

    private void refreshPools() {
        List all = pools();
        for ( int i = 0; i < all.size(); i++ ) {
            AgentPool pool = (AgentPool) all.get( i );
            if ( pool.isEmpty() ) {
                pools.remove( pool.getId() );
                sendPoolDestroyNotification( pool );
                pool.dispose();
            }
        }
    }

    /**
     * Returns a list of all available AgentPools in this service memory
     *
     * @return List<AgentPool>
     */
    public List pools() {
        Object[] content = pools.values().toArray();
        List ps = new ArrayList();
        for ( int i = 0; i < content.length; i++ ) {
            ps.add( content[i] );
        }
        return Collections.unmodifiableList( ps );
    }

    public Set<Agent> listPoolAgents() {
        Set<Agent> agents = new HashSet<Agent>();
        for ( AgentPool pool : pools.values() ) {
            agents.addAll( pool.agentView() );
        }
        return agents;
    }

    public synchronized void unassignPoolMemberships( Agent agent ) {
        Iterator pooliter = pools.values().iterator();
        synchronized ( pools ) {
            while ( pooliter.hasNext() ) {
                AgentPool ap = (AgentPool) pooliter.next();
                removeAgentFromPool( agent, ap );
                sendUnassignedIm( agent, ap );
            }
        }
        refreshPools();
    }

    public void removeAgentFromPool( Agent agent, AgentPool ap ) {
        if ( ap.exists( agent ) ) {
//                    agent.setActive(false);
            logger.finer( "Removing agent '" + agent.getAOR() + "' from pool " + ap );
            ap.remove( agent );
            sendPoolUpdateNotification( ap );
            refreshPools();
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .getOrAddQueueActivityLogger( ap.getName() ).setOnlineAgents( ap.size() );

                getServiceFactory().getLogService().getServiceActivityLogger().setOnlineAgents( inServiceAgentsSize() );
            } catch ( ActivityLogNotExistsException e ) {
                logger.severe( e.getMessage() );
            }
        }
    }

    private void sendUnassignedIm( Agent agent, AgentPool pool ) {
        try {
            Presence contact = getServiceFactory().getPresenceService().getPresence( new UserAgent( agent.getAOR() ) );
            getServiceFactory().getSipService().sendIM( contact,
                    ResourceBundleUtil.getMessage( "callmanager.supportGroup.disable", pool.getName() )
            );
        } catch ( OpxiException e ) {
            logger.severe( e.getMessage() );
        }
    }

    public synchronized void assignPoolMemberships( Agent agent ) {
        List skills = agent.getPoolMemberships();
        logger.finer( "Pool membership size is " + skills.size() + " for agent " + agent.getAOR() );
        for ( int i = 0; i < skills.size(); i++ ) {
            try {
                String poolId = (String) skills.get( i );
                AgentPool agentPool = null;
                if ( !poolAvailable( poolId ) ) {
                    agentPool = createPool( poolId );
                    logger.finer( "Created pool: " + agentPool );
                    pools.put( poolId, agentPool );
                    //                if( agentPool == null ) {
                    //                    return;
                    //                }
                } else {
                    agentPool = getPool( poolId );
//                    logger.finer( "Fetching pool: " + agentPool );
                }
                if ( isInPrimaryGroup( agent, agentPool ) ) {
                    if ( addAgentToPool( agent, agentPool ) ) {
                        sendAgentAssignedIm( agent, agentPool );
                    }
                } else {
                    logger.finest( "Agent '" + agent + "' is not in primary group for skillPool '" + agentPool + "'" );
                }
            } catch ( OpxiException e ) {
                logger.log( Level.WARNING, e.getMessage(), e );
                logger.warning( "Try next pool..." );
                try {
                    Presence contact = getServiceFactory().getPresenceService().getPresence( new UserAgent( agent.getAOR() ) );
                    getServiceFactory().getSipService().sendIM( contact,
                            ResourceBundleUtil.getMessage( "callmanager.generic.error", e.getMessage() ) );
                } catch ( Exception e1 ) {
                    logger.log( Level.SEVERE, e1.getMessage(), e1 );
                }
            }
        }
    }

    private void sendAgentAssignedIm( Agent agent, AgentPool agentPool ) {
        // agent's added to some pool
        try {
            Presence contact = getServiceFactory().getPresenceService().getPresence( new UserAgent( agent.getAOR() ) );
            getServiceFactory().getSipService().sendIM( contact,
                    ResourceBundleUtil.getMessage( "callmanager.presence.onlineMsg", agentPool.getName() ), 5 );
            agentPool.imUsageHistory( agent.getAOR() );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    private boolean addAgentToPool( Agent agent, AgentPool agentPool ) throws UserNotAvailableException, NoActivePublishContextException {
        if ( !agentPool.exists( agent ) ) {
            logger.finer( "Adding agent " + agent.getAOR() + " to pool " + agentPool );
            agentPool.add( agent );
            sendPoolUpdateNotification( agentPool );
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .getOrAddQueueActivityLogger( agentPool.getName() ).setOnlineAgents( agentPool.size() );

                getServiceFactory().getLogService().getServiceActivityLogger().setOnlineAgents( inServiceAgentsSize() );


            } catch ( ActivityLogNotExistsException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
            return true;

        }
        return false;
    }

    private boolean isInPrimaryGroup( Agent agent, AgentPool pool ) {
        if ( pool instanceof SkillBasedPool ) {
            if ( pool.getProfile().getOpxiCMEntityProfileChoice()
                    .getPoolTargetProfile().getQueueProfile( 0 ).getSupportGroupCount() > 0 ) {
                String primary = pool.getProfile().getOpxiCMEntityProfileChoice()
                        .getPoolTargetProfile().getQueueProfile( 0 ).getSupportGroup( 0 ).getName();
                try {
                    primary = BaseDAOFactory.getDirectoryDAOFactory().getPoolTargetDAO().getCNForDN( primary );
                    if ( !agent.isMemeber( primary ) ) {
                        return false;
                    }
                } catch ( OpxiException e ) {
                    logger.log( Level.SEVERE, e.getMessage(), e );
                }
            }
        }
        return true;
    }

    public void addSupportGroup( String queueName, String groupName ) {
//        logger.finest( "************* add group " + groupName + " to queue " + queueName );
        try {
            List agents = getPool( groupName ).agentView();
            for ( int i = 0; i < agents.size(); i++ ) {
                Agent agent = (Agent) agents.get( i );
                addAgentToPool( agent, getPool( queueName ) );
            }
        } catch ( NoPoolAvailableException e ) {
            getServiceFactory().getSipService().sendAdminIM( "Couldn't load support group "
                    + groupName
                    + " for "
                    + queueName
                    + ": " + e.getMessage()
            );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }


    public void removeSupportGroup( String queueName, String groupName ) {
//        logger.finest( "************* remove group " + groupName + " from queue " + queueName );
        try {
            List agents = getPool( groupName ).agentView();
            for ( int i = 0; i < agents.size(); i++ ) {
                removeAgentFromPool( (Agent) agents.get( i ), getPool( queueName ) );
            }
        } catch ( NoPoolAvailableException e ) {
            // Iradi nadare! in ye baro hala Swallow mikonim!
        }
    }


    private void sendPoolUpdateNotification( AgentPool pool ) {
        UserAgent poolAgent = new UserAgent( getServiceFactory().getSipService().createSipURIForName( pool.getName() + "Skill" ) );
        Presence poolStatus = new Presence( poolAgent );
        poolStatus.setBasic( SIPConstants.BASIC_STATUS_OPEN );
        poolStatus.setNote( pool.size() + " Agent(s) Online" );
        poolStatus.setActivity( Presence.ACTIVITY_UNKNOWN );
        try {
            getServiceFactory().getPresenceService().servicePresence( poolStatus );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    private void sendPoolDestroyNotification( AgentPool pool ) {
        UserAgent poolAgent = new UserAgent( getServiceFactory().getSipService().createSipURIForName( pool.getName() + "Skill" ) );
        Presence poolStatus = new Presence( poolAgent );
        poolStatus.setBasic( SIPConstants.BASIC_STATUS_CLOSED );
        poolStatus.setActivity( Presence.ACTIVITY_AWAY );
        poolStatus.setNote( "No Agents Online" );
        try {
            getServiceFactory().getPresenceService().servicePresence( poolStatus );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    private int inServiceAgentsSize() {
        Set agents = new HashSet();
        Iterator it = pools().iterator();
        while ( it.hasNext() ) {
            AgentPool pool = (AgentPool) it.next();
            if ( pool instanceof SkillBasedPool ) {
                agents.addAll( pool.agentView() );
            }
        }
        return agents.size();
    }

    private boolean poolAvailable( String id ) {
        return pools.containsKey( id );
    }

    /**
     * Hunts an Agent object from the pools loaded in this service for
     * the input data.
     * <p/>
     * The generic algorithm to hunt an Agent is as below:
     * 1. Identify the <i>primary</i> pool for the specified queue
     * 2. Select a primary AgentSelection from that pool (containing initial scores for each)
     * 3. Iterate all other loaded pools and check for secondary matches
     * 3.1. If any secondary pool matched, refine the Agent's scores based on the intersection
     * of secondary pool with primary one.
     *
     * @param queue The Queue which is responsible to handle the call
     * @param call  The CallService object that represents the call
     * @return Hunted Agent object from the pool
     * @throws NoPoolAvailableException if no pool is loaded to handle calls from input queue
     * @throws NoIdleAgentException     if no IDLE agent to handle this call exists
     */
    public Agent huntAgent( Queue queue, CallService call ) throws NoPoolAvailableException, NoIdleAgentException {
        // call.getHandlerQueueName() OR queue.getName()
        AgentPool primaryPool = getPool( queue.getName() );
        logger.finer( "Primary pool for " + queue.getName() + ": " + primaryPool );
//        if( primaryPool instanceof SkillBasedPool ) {
        AgentSelection selection = primaryPool.select( call );
        logger.finer( "Primary selection: " + selection );
        Iterator allPools = pools.values().iterator();
        synchronized ( allPools ) {
            while ( allPools.hasNext() ) {
                AgentPool pool = (AgentPool) allPools.next();
                if ( !pool.getName().equals( primaryPool.getName() ) ) {
                    pool.select( selection );
                }
            }
        }
//        logger.finer( "Going to hunt:" );
        return primaryPool.hunt( selection );
    }

    public void authorizeNextUsage( String poolName, String ruleId, UserAgent ua ) throws OpxiException {
        AgentPool pool = getPool( poolName );
        if ( pool instanceof WorkgroupAgentPool ) {
            ((WorkgroupAgentPool) pool).authorizeNextUse( ruleId, ua );
        }
    }

    /**
     * Notifies Agent objects for profile updates
     *
     * @param profile an agent profile
     */
    public void agentProfileUpdateNotification( OpxiCMEntityProfile profile ) {
        getServiceFactory().getAgentService().agentProfileUpdateNotification( profile );
    }

    /**
     * Notifies loaded Pool objects for profile updates
     *
     * @param profile a pool profile
     */
    public void poolProfileUpdateNotification( OpxiCMEntityProfile profile ) {
        try {
            String poolName = getDAOFactory().getPoolTargetDAO().getCNForDN( profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().getDN() );
            getPool( poolName ).assignProfile( profile );
            getServiceFactory().getQueueManagementService().queueForName( poolName ).assignProfile( profile );
        } catch ( NoPoolAvailableException e ) {
            logger.warning( e.getMessage() );
        } catch ( QueueNotExistsException e ) {
            logger.warning( e.getMessage() );
        } catch ( QueueManagerException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        } catch ( DAOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        } catch ( ProfileException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

    public List listObjects() {
        return Arrays.asList( pools.values().toArray() );
    }

    public void destroy() {
        List all = pools();
        for ( int i = 0; i < all.size(); i++ ) {
            AgentPool pool = (AgentPool) all.get( i );
            pool.dispose();
        }
        pools.clear();
//        getServiceFactory().getAgentService().dispose();
        logger.info( "PoolService destroyed successfully." );
    }

    public List getPools( String agentAOR ) throws AgentNotAvailableException {
        List pools = new ArrayList();
        Agent agent = getServiceFactory().getAgentService().getAgentByAOR( agentAOR );
        List poolIdList = agent.getPoolMemberships();
        for ( int i = 0; i < poolIdList.size(); i++ ) {
            try {
                AgentPool pool = getPool( (String) poolIdList.get( i ) );
                if ( pool.exists( agent ) ) {
                    pools.add( pool.getName() );
                }
            } catch ( NoPoolAvailableException e ) {
                logger.warning( e.getMessage() );
            }
        }
        return pools;
    }
}