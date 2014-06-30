package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.call.AbstractTimerContext;
import com.basamadco.opxi.callmanager.call.TimerException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.entity.profile.types.RepeatType;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 23, 2010
 *         Time: 1:52:11 PM
 */
public class ShiftTimer extends AbstractTimerContext {

    private static final Logger logger = Logger.getLogger( ShiftTimer.class.getName() );

    private WorkgroupAgentPool pool;


    public ShiftTimer( ServiceFactory serviceFactory, String id, WorkgroupAgentPool pool, Calendar finish ) {
        super( serviceFactory, id );
        this.pool = pool;
        long later = finish.getTimeInMillis() - System.currentTimeMillis();
        setTimer( getServiceFactory().getSipService().getTimerService().createTimer(
                getServiceFactory().getSipService().getApplicationSession(),
                later, false, this )
        );
        logger.finest( "Created shift finish timer for[" + OpxiToolBox.duration( later ) + "]: " + finish.getTime() );
    }


    public void timeout() throws TimerException {
        pool.unAssignAgents();
    }
}
