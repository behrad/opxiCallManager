package com.basamadco.opxi.callmanager.sip.listener;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.call.LegState;
import com.basamadco.opxi.callmanager.sip.AbstractContext;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipSession;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Call Manager uses a mimimum treshhold for SipApplicationSessions to efficiently
 * manage memory and resources. So this class implements SipApplicationSessionListener
 * to be notified for the SipApplicationSession lifecycle events.
 *
 * @author Jrad
 *         Date: Aug 31, 2006
 *         Time: 11:36:25 AM
 */
public class ApplicationSessionManager implements SipApplicationSessionListener {

    private static final Logger logger = Logger.getLogger(ApplicationSessionManager.class.getName());


    private static final int DEFAULT_APPSESSION_EXPIRY = Integer.parseInt(
            PropertyUtil.getProperty("opxi.callmanager.sip.appsessionExpiryInterval")
    );

    public void sessionCreated(SipApplicationSessionEvent sipApplicationSessionEvent) {
    }


    public void sessionDestroyed(SipApplicationSessionEvent sipApplicationSessionEvent) {
    }

    /**
     * Checks if the SipApplicationSession to be expired is envolved with a CallService,
     * if so it will postpone the session expiry for another period.
     *
     * @param sipApplicationSessionEvent
     */
    public void sessionExpired(SipApplicationSessionEvent sipApplicationSessionEvent) {
        SipApplicationSession appSession = sipApplicationSessionEvent.getApplicationSession();
        Leg leg = null;
        if (appSession != null) {
            boolean expiryFlag = true;
            AbstractContext appSessionMgt = (AbstractContext)appSession.getAttribute(AbstractContext.class.getName());
            if (appSessionMgt != null) {
                logger.finest("Postpone appSession expiry for " + appSession + " in context '" + appSessionMgt + "'");
                /*Iterator sessions = appSession.getSessions("SIP");
                logger.finest("++++Contained SipSession callIds in this appSession are: ");
                while (sessions.hasNext()) {
                    SipSession sipSession = (SipSession) sessions.next();
                    logger.finest("++++ '" + sipSession.getCallId() + "'");
                }*/
                appSession.setExpires(DEFAULT_APPSESSION_EXPIRY);
                expiryFlag = false;
            } else {
                Iterator sessions = appSession.getSessions("SIP");
                while (sessions.hasNext()) {
                    SipSession session = (SipSession) sessions.next();
                    try {
                        if (session != null) {
                            // 1. Check to see if this session is associated with an active call
                            leg = (Leg) session.getAttribute( Leg.class.getName() );
                            if (leg != null) { // this session is accociated with a sip leg
                                if( leg.isIdle() ) {
                                    // associated leg object is already disposed,
                                    // so it should be cleaned up from sipSession
                                    session.removeAttribute( Leg.class.getName() );
                                } else {
                                    if ( leg.isAlive() ) {
                                        logger.finest("Postpone expiry for " + appSession + ", active call sipSession: " + session.getCallId());
                                        appSession.setExpires(DEFAULT_APPSESSION_EXPIRY);
                                        expiryFlag = false;
                                        break;
                                    } else {
                                        logger.warning("Try to terminate non-active leg: " + leg );
                                        leg.setState(LegState.IDLE);
    //                                    leg.dispose();
                                    }
                                }
                            }
                        } else {
                            logger.finest("WOW! How come? The sipSession is " + session );
                        }
                        //                    logger.finer( associatedLeg );
                        //                    logger.finer( associatedLeg.getCallService() );
                        // Above means that we still have an active Leg assigned to the sip session

                    } catch ( IllegalStateException e ) {
                        logger.log( Level.WARNING, "Invalid SipSession: " + session.getId() , e );
                    }
                }
            }
            if ( expiryFlag ) {
                if( leg != null ) {
                    logger.fine( "Expired appSession '" + appSession +
                            "' involved in call " + leg.getCallService() );
                } else {
                    logger.fine("Expired appSession '" + appSession  + "', NO reason!");
                }
            } else {
//                logger.finest( "What has happened to appSession '" + appSession + "'?" );
            }
        }
    }

}
