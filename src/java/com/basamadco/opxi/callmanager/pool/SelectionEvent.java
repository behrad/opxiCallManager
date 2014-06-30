package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.pool.Agent;

/**
 * @author Jrad
 *         Date: Mar 4, 2006
 *         Time: 4:56:26 PM
 */
public class SelectionEvent {

    private Agent agent;

    private float skillScore;

    private float finalScore;

    public SelectionEvent( Agent agent, float skillScore ) {
        this.agent = agent;
        this.skillScore = skillScore;
    }

    public Agent getAgent() {
        return agent;
    }

    public float getSkillScore() {
        return skillScore;
    }

    public void addSkillScore( float score ) {
        skillScore += score;
    }

    public void setSkillScore( float score ) {
        skillScore = score;
    }


    public float getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(float finalScore) {
        this.finalScore = finalScore;
    }

    public String toString() {
        return "SelectionEvent[Agent:"+agent.getAOR()+", SkillScore="+skillScore+", FinalScore=" + finalScore + "]";
    }
}
