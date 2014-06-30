package com.basamadco.opxi.callmanager.pool;

import java.util.logging.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.TreeSet;

/**
 * @author Jrad
 *         Date: Sep 3, 2006
 *         Time: 3:05:40 PM
 */
public class LIARPoolContainer implements PoolAgentContainer {

    private static final Logger logger = Logger.getLogger( LIARPoolContainer.class.getName() );


    private final LinkedList<Agent> agents = new LinkedList<Agent>();

    private final Object AGENTS_LOCK = new Object();


    public Agent next() {
        return (Agent) agents.getFirst();
    }

    public void addAgent( Agent agent ) {
        synchronized ( AGENTS_LOCK ) {
            agents.addFirst( agent );
        }
    }

    public boolean removeAgent( Agent agent ) {
        synchronized ( AGENTS_LOCK ) {
            return agents.remove( agent );
        }
    }

    public int size() {
        return agents.size();
    }

    public List<Agent> agentView() {
        return Collections.unmodifiableList( agents );
    }

    public float localRank( Agent agent ) {
        return normalize( agent.idleTime() / 1000 );
//        synchronized( AGENTS_LOCK ) {
//            return ( agents.indexOf( agent ) + 1 ) / ( agents.size() );
//        }
    }


    private float normalize( long duration ) {
        return (float) duration / (float) maxIdleTime();
    }

    private long maxIdleTime() {
        long max = 0;
        synchronized ( AGENTS_LOCK ) {
            for ( Agent a : agents ) {
                if ( a.idleTime() > max ) {
                    max = a.idleTime();
                }
            }
        }
        return max / 1000;
    }

    public void agentHunted( Agent agent ) {
        if ( exists( agent ) ) {
            synchronized ( AGENTS_LOCK ) {
                agents.remove( agent );
                agents.addLast( agent );
            }
        }
    }

    public boolean exists( Agent agent ) {
        synchronized ( AGENTS_LOCK ) {
            return agents.contains( agent );
        }
    }

    public boolean isEmpty() {
        synchronized ( AGENTS_LOCK ) {
            return agents.size() == 0;
        }
    }

    public String toString() {
        return getClass().getName();
    }

    public void dispose() {
        agents.clear();
    }

}
