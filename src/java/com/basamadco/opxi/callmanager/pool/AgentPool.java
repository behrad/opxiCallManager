package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.rule.Rule;
import com.basamadco.opxi.callmanager.rule.RuleUsage;
import com.basamadco.opxi.callmanager.entity.UserAgent;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * To handle calls for any particular queue there must be a collection
 * of Agents who systematically or <i>semantically</i> are grouped together.
 * The AgentPool is responsible to represent those groupings and provides
 * services like primary and secondary Agent selection and hunt which selects
 * a final <i>best</i> choice from Agent selections.
 * Each AgentPool internally has an PoolAgentContainer for Agents' storage.
 *
 * @author Jrad
 *         Date: Feb 20, 2006
 *         Time: 3:56:45 PM
 */
public abstract class AgentPool extends PoolTarget {


    private static final Logger logger = Logger.getLogger( AgentPool.class.getName() );

//    protected PoolTarget target; // Refactored to Inheritance! No more choices with less changes

    private PoolAgentContainer agentContainer;


    protected AgentPool( PoolTarget target, PoolAgentContainer agentContainer )
            throws PoolInitializationException, ProfileException {
        super( target );
        this.agentContainer = agentContainer;
    }

    /**
     * Selects a primary Agent selection for the specified call
     *
     * @param call The call for which Agents will be selected
     * @return AgentSelection object representing the Agents and their scores
     * @see com.basamadco.opxi.callmanager.pool.AgentSelection
     */
    public abstract AgentSelection select( com.basamadco.opxi.callmanager.call.CallService call );

    /**
     * Refines the specified selection, improving scores for Agents that exist in
     * both selection and this pools Agent container.
     *
     * @param selection The primary Agent selection selected from another AgentPool
     * @return The refined version of the selection with improved scores for Agents
     *         that are common between selection and this AgentPool's Agent container
     */
    public abstract AgentSelection select( AgentSelection selection );

    /**
     * Hunts an Agent from the input selection.
     * This first normalizes the input selection, then sorts selected listAgents
     * in that selection based on their final scores. finally chooses Agents
     * from the head until it finds one idle Agent and returns that one
     *
     * @param selection
     * @return Hunted Agent object
     * @throws NoIdleAgentException If no idle Agent can be choosen from the
     *                              selection
     */
    public abstract Agent hunt( AgentSelection selection ) throws NoIdleAgentException;


    protected PoolAgentContainer getAgentContainer() {
        return agentContainer;
    }

    /**
     * Adds the agent to this pools AgentContainer then adds this pool
     * to agent's hunt event observers list.
     *
     * @param agent the Agent to add
     */
    void add( Agent agent ) {
        getAgentContainer().addAgent( agent );
        agent.hunting().addObserver( huntingFan );
    }

    /**
     * Removes the Agent from this pools AgentContainer. Also removes this
     * pool from agent's hunt event observers list.
     *
     * @param agent the Agent ro romove
     */
    void remove( Agent agent ) {
        agent.hunting().deleteObserver( huntingFan );
        getAgentContainer().removeAgent( agent );
    }

    /**
     * Receives a hunt event for the specified Agent. This internally
     * delegates this event to this pool's AgentContainer.
     *
     * @param agent The Agent which is hunted
     */
    protected void agentHunted( Agent agent ) {
        getAgentContainer().agentHunted( agent );
    }

    /**
     * Size of Agent's available in this pool
     *
     * @return loaded listAgents size
     */
    public int size() {
        return getAgentContainer().size();
    }

    /**
     * Checks if the agent is loaded in this pool
     *
     * @param agent the agent to check
     * @return true if the Agent object is loaded into this pool
     */
    public boolean exists( Agent agent ) {
        return getAgentContainer().exists( agent );
    }

    public List agentView() {
        return getAgentContainer().agentView();
    }

    public String agentViewStr() {
        List agents = agentView();
        StringBuffer buff = new StringBuffer( "[" );
        for ( int i = 0; i < size(); i++ ) {
            Agent agent = (Agent) agents.get( i );
            buff.append( "'" + agent.getAOR() + "', " );
        }
        buff.append( "]" );
        return buff.toString();
    }

    public boolean isEmpty() {
        return getAgentContainer().isEmpty();
    }

    public String toString() {
        return OpxiToolBox.unqualifiedClassName( getClass() ) +
                "[Target='" + getName() +
                "', Size=' + " + size() +
                "', Members=" + agentViewStr() +
                "', hashCode='" + hashCode() +
                "']";
    }


    public void dispose() {
        agentContainer.dispose();
    }

    protected HuntingObserver huntingFan = new HuntingObserver();

    public void imUsageHistory( String aor ) {
        // IM agent usage history for this pool rule set
        for ( Rule rule : getRules() ) {
            logger.finest( "Checking Rule " + rule.getRuleInfo() + " for history" );
            UserAgent ua = new UserAgent( aor );
            RuleUsage usage = rule.getHistory( ua );
            if ( usage != null ) {
                usage.sendIM( false );
            } else {
                logger.finest( "No usage history found for " + ua.getSipURIString() );
            }
        }
    }

    public void unAssignAgents() {
        Iterator it = getAgentContainer().agentView().iterator();
        while ( it.hasNext() ) {
            Agent agent = (Agent) it.next();
            getServiceFactory().getPoolService().removeAgentFromPool( agent, this );
        }
    }

    private class HuntingObserver implements Observer {

        public final void update( Observable observable, Object object ) {
            if ( !(object instanceof Agent) ) {
                throw new IllegalArgumentException( "Passed object is not a Agent: " + object );
            }
            Agent agent = (Agent) object;
            agentHunted( agent );
        }

    }

}