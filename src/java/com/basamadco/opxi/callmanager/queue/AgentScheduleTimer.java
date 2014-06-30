package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.pool.Agent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Jan 11, 2009
 *         Time: 6:06:04 PM
 */
public class AgentScheduleTimer extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger( AgentScheduleTimer.class.getName() );

    Agent agent;

    public AgentScheduleTimer( Agent agent, long restTime ) {
        super( agent.getServiceFactory(), "REST-TIMER::" + agent.getAOR() );
        this.agent = agent;
        setTimer( agent.getServiceFactory().getSipService().getTimerService().createTimer(
                agent.getServiceFactory().getSipService().getApplicationSession(),
                restTime * 1000,
                false,
                this
        )
        );
    }

    public void timeout() throws TimerException {
        logger.finest( "Rest time finished for '" + agent.getAOR() + "'" );
        agent.finishRest();
    }

}
