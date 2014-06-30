package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.call.CallService;

/**
 * A simple encapsulation to represent context of a CallService and
 * selected (agent, score) pairs.
 * This class is also responsible for normalizing selections' score
 *
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 10:22:46 AM
 */
public class AgentSelection {

    private String source;

    private CallService call;

    private SelectionEvent[] selections;

    private float max;

    private float min;


    public AgentSelection( String source, CallService call, SelectionEvent[] selections ) {
        this.source = source;
        this.call = call;
        this.selections = selections;
    }

    /**
     * The AgentPool name which has created this selection
     *
     * @return This selection's source AgentPool
     */
    public String getSource() {
        return source;
    }

    /**
     * The call context for which this selection is created
     *
     * @return
     */
    public CallService getCall() {
        return call;
    }

    /**
     * List of selected Agents for this selection
     *
     * @return An array of SelectionEvent objects
     * @see com.basamadco.opxi.callmanager.pool.SelectionEvent
     */
    public SelectionEvent[] selections() {
        return selections;
    }

    /**
     * Normalizes the listAgents score. Currently uses min-max normalization
     */
    public void normalize() {
        setMinMax();
        if ( min != max ) {
            for ( int i = 0; i < selections.length; i++ ) {
                selections[i].setSkillScore( (selections[i].getSkillScore() - min) / (max - min) );
            }
        }
    }

    private void setMinMax() {
        for ( int i = 0; i < selections.length; i++ ) {
            if ( selections[i].getSkillScore() > max ) {
                max = selections[i].getSkillScore();
            }
            if ( selections[i].getSkillScore() < min ) {
                min = selections[i].getSkillScore();
            }
        }
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer( "AgentSelection[" );
        for ( int i = 0; i < selections.length; i++ ) {
            buffer.append( selections[i].getAgent().getName() + "=" + selections[i].getFinalScore() );
            if ( i != selections.length - 1 ) {
                buffer.append( ", " );
            }
        }
        buffer.append( "]" );
        return buffer.toString();
    }
}