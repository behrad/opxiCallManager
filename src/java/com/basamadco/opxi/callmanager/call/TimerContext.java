package com.basamadco.opxi.callmanager.call;

import java.io.Serializable;

/**
 * this is OPXi Call Manager common timer interface which is to simplify the standard timer
 * facility of the SIP Servlet API (JSR116). All event Objects passed to the createTimer method
 * of the TimerService should be subclasses of this Interface to be catched by the
 * CallManagerTimerListener.
 *
 * @author Jrad
 *         Date: Nov 18, 2006
 *         Time: 10:47:29 AM
 * @see com.basamadco.opxi.callmanager.sip.listener.CallManagerTimerListener
 */
public interface TimerContext extends Serializable {

    /**
     * This is called when this timer context's timer has been timed out by the
     * CallManagerTimerListener's timeout method.
     *
     * @throws com.basamadco.opxi.callmanager.call.TimerException
     *          When sth. goes wrong
     */
    public void timeout() throws com.basamadco.opxi.callmanager.call.TimerException;


    public void cancel();

    public String getId();

}
