package com.basamadco.opxi.callmanager.queue;

import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.ServiceFactory;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Dec 7, 2008
 *         Time: 11:10:05 AM
 */
public class IdleTimerToSchedule extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger(IdleTimerToSchedule.class.getName());

    private Queue queue;

    private String agentAOR;

    public IdleTimerToSchedule(Queue queue, String agentAOR) {
        super(queue.getServiceFactory(), queue.getId() + "::" + agentAOR);
        this.queue = queue;
        this.agentAOR = agentAOR;
        setTimer(queue.getServiceFactory().getSipService().getTimerService().createTimer(
                queue.getServiceFactory().getSipService().getApplicationSession(),
                queue.getIdleTimeToSchedule() * 1000,
                false,
                this
        )
        );
    }

    public void timeout() throws TimerException {
        logger.finest("Schedule Call ['" + queue.getName() + "', '" + agentAOR + "']");
        queue.scheduleCallFor(agentAOR);
    }
}
