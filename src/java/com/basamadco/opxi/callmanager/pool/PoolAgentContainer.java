package com.basamadco.opxi.callmanager.pool;

import java.util.List;

/**
 * @author Jrad
 *         Date: Sep 3, 2006
 *         Time: 2:59:43 PM
 */
public interface PoolAgentContainer {

    public Agent next();

    public void agentHunted( Agent agent );

    public void addAgent( Agent agent );

    public boolean removeAgent( Agent agent );

    public float localRank( Agent agent );

    public boolean exists( Agent agent );

    public int size();

    public List<Agent> agentView();

    public boolean isEmpty();

    public void dispose();

}
