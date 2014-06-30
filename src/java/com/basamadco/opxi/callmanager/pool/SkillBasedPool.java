package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.pool.rules.AbstractMatchingRule;
import com.basamadco.opxi.callmanager.pool.rules.MatchingRule;
import com.basamadco.opxi.callmanager.pool.rules.RuleInstantiationException;
import com.basamadco.opxi.callmanager.profile.ProfileException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 3, 2006
 *         Time: 3:18:34 PM
 */
public abstract class SkillBasedPool extends AgentPool implements Comparator {

    private static final Logger logger = Logger.getLogger( SkillBasedPool.class.getName() );


    //    private Rule matchingRule;
    private MatchingRule matchingRule;

    private float primarySkillScoreCoefficient;

    private float secondarySkillScoreCoefficient;

    private float qualityScoreCoefficient = Float.parseFloat(
            PropertyUtil.getProperty( "opxi.callmanager.skill.qualityScoreCoefficient" ) );

    private float efficiencyScoreCoefficient = Float.parseFloat(
            PropertyUtil.getProperty( "opxi.callmanager.skill.efficiencyScoreCoefficient" ) );


    protected SkillBasedPool( PoolTarget poolTarget, PoolAgentContainer agentContainer )
            throws PoolInitializationException, ProfileException {
        super( poolTarget, agentContainer );
        primarySkillScoreCoefficient = getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().getSkillProfile().getPSSC();
        secondarySkillScoreCoefficient = getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().getSkillProfile().getSSSC();
        try {
            matchingRule = AbstractMatchingRule.instantiate(
                    getProfile().getOpxiCMEntityProfileChoice().getPoolTargetProfile().getPoolTargetProfileChoice().getSkillProfile().getMatchingRule() );
//            matchingRule =  Rule.parse( ruleXML, getTarget() );
        } catch ( RuleInstantiationException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new PoolInitializationException( e );
        }
    }

    public int compare( Object object, Object object1 ) {
        if ( !(object instanceof SelectionEvent) || !(object1 instanceof SelectionEvent) ) {
            throw new IllegalArgumentException( "AgentPool comparator only accepts SelectionEvent instances" );
        }
        SelectionEvent event1 = (SelectionEvent) object;
        SelectionEvent event2 = (SelectionEvent) object1;
        if ( event1.getAgent().openCalls() < event2.getAgent().openCalls() ) {
            return -1;
        } else if ( event1.getAgent().openCalls() > event2.getAgent().openCalls() ) {
            return +1;
        } else {
            float rank = getAgentContainer().localRank( event1.getAgent() );
            float score1 = event1.getSkillScore() * qualityScoreCoefficient +
                    rank * efficiencyScoreCoefficient;
            logger.finest(
                    "Score[" + event1.getAgent().getName() + "]: (" + event1.getSkillScore() + ", " + rank + ")"
            );
            event1.setFinalScore( score1 );
            float rank2 = getAgentContainer().localRank( event2.getAgent() );
            float score2 = event2.getSkillScore() * qualityScoreCoefficient +
                    rank2 * efficiencyScoreCoefficient;
            event2.setFinalScore( score2 );
            logger.finest(
                    "Score[" + event2.getAgent().getName() + "]: (" + event2.getSkillScore() + ", " + rank2 + ")"
            );
            return (score1 < score2) ? +1 : (score1 == score2 ? 0 : -1);
        }
    }

    public AgentSelection select( CallService call ) {
        List currentAgents = agentView();
        SelectionEvent[] selections = new SelectionEvent[currentAgents.size()];
        for ( int i = 0; i < currentAgents.size(); i++ ) {
            Agent agent = (Agent) currentAgents.get( i );
//            logger.finer( "1: " + agent.getAOR() + ":["+getTarget().getId()+"] " + agent.skillEfficiency( getTarget().getId() ) );
//            logger.finer( "1: " + agent.getAOR() + ":["+getTarget().getName()+"] "+ agent.skillEfficiency( getTarget().getName() ) );
            // TODO add exceptional flow to control bad agent data
            float score = getPrimarySkillScoreCoefficient() * agent.skillEfficiency( getId() );
            selections[i] = new SelectionEvent( agent, score );
//            agent.notifySelection( selections[ i ] );
        }
        return new AgentSelection( getName(), call, selections );
    }

    public AgentSelection select( AgentSelection selection ) {
        logger.finest( "Checking " + getMatchingRule().getRuleInfo() + " as secondary skill: " + this );
        if ( getMatchingRule().evaluate( selection.getCall().getInitialRequest() ) ) {
            logger.finer( this + " is matched for secondary skill with weight=" + getSecondarySkillScoreCoefficient() );
            for ( int i = 0; i < selection.selections().length; i++ ) {
                SelectionEvent se = selection.selections()[i];
                if ( getAgentContainer().exists( se.getAgent() ) ) {
                    se.addSkillScore( se.getAgent().skillEfficiency( getId() ) * getSecondarySkillScoreCoefficient() );
                }
            }
        }
        return selection;
    }

    public Agent hunt( AgentSelection selection ) throws NoIdleAgentException {
        logger.finest( "Before sort selection: " + selection );
        selection.normalize();
        SelectionEvent[] selections = selection.selections();
        Arrays.sort( selections, this );
        logger.finest( "Final selection: " + selection );
        Agent currAgent = null;
        for ( int i = 0; i < selections.length; i++ ) {
            currAgent = selections[i].getAgent();
//            synchronized (LockManager.getLockById(currAgent.getAOR())) {
            if ( getAgentContainer().exists( currAgent ) ) {
                try {
                    currAgent.assignCall( selection.getCall() );
                    return currAgent;
                } catch ( AgentNotIdleException e ) {
                    logger.finer( e.getMessage() + ", trying next agent..." );
                }
            }
//            }
        }
        throw new NoIdleAgentException( getName() + ": " + selections.length );
    }


    public MatchingRule getMatchingRule() {
        return matchingRule;
    }

    public float getPrimarySkillScoreCoefficient() {
        return primarySkillScoreCoefficient;
    }

    public void setPrimarySkillScoreCoefficient( float primarySkillScoreCoefficient ) {
        this.primarySkillScoreCoefficient = primarySkillScoreCoefficient;
    }

    public float getSecondarySkillScoreCoefficient() {
        return secondarySkillScoreCoefficient;
    }

    public void setSecondarySkillScoreCoefficient( float secondarySkillScoreCoefficient ) {
        this.secondarySkillScoreCoefficient = secondarySkillScoreCoefficient;
    }

    public float getQualityScoreCoefficient() {
        return qualityScoreCoefficient;
    }

    public void setQualityScoreCoefficient( float qualityScoreCoefficient ) {
        this.qualityScoreCoefficient = qualityScoreCoefficient;
    }

    public float getEfficiencyScoreCoefficient() {
        return efficiencyScoreCoefficient;
    }

    public void setEfficiencyScoreCoefficient( float efficiencyScoreCoefficient ) {
        this.efficiencyScoreCoefficient = efficiencyScoreCoefficient;
    }

}
