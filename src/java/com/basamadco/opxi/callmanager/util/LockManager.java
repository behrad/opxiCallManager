package com.basamadco.opxi.callmanager.util;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.entity.UserAgent;

import javax.servlet.sip.SipSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;


/**
 * @author Jrad
 *         Date: May 7, 2006
 *         Time: 4:59:58 PM
 */
public class LockManager {

    private static final Logger logger = Logger.getLogger(LockManager.class.getName());

    private static final Map locks = new WeakHashMap(113);

    public static synchronized Object getLockById(String lock_id) {
        Object lock = locks.get(lock_id);
        final String lockIdCopy = new String(lock_id);
        if (lock == null) {
            lock = new Object() {
                public String toString() {
                    return "Lock[key=" + lockIdCopy + ", instance=" + super.toString() + "]";
                }
            };
            locks.put(lock_id, lock);
        }
        return lock;
    }


    private static String CANCEL_200 = "CANCEL-200-RACE-FOR-";

    public static Object getCancel_200_RaceLock(CallService call) {
        return getLockById(CANCEL_200 + call.getId());
    }

    private static String SESSION_SYNC = "SIP_SESSION_SYNC";

    public static synchronized Object getSessionLock(final SipSession session) {
//    	logger.finest( "Obtaining lock for session " + session.getId() + " with callId '" + session.getCallId() + "'" );
        Object lock = session.getAttribute(SESSION_SYNC);
        if (lock == null) {
            lock = new Object() {
                public String toString() {
                    return "SipSessionLock[key=" + session.getId() + ", instance=" + super.toString() + "]";
                }
            };
            session.setAttribute(SESSION_SYNC, lock);
        }
//    	logger.finest( "Obtained lock for session with callId '" + session.getCallId() + "' is: " + lock );
        return lock;
//        return getLockById( SESSION_SYNC + session.getId() );
    }

    private static String REJECT_SCHEDULE = "Reject-Schedule-";

    public static Object getRejectScheduleLock(CallService call) {
        return getLockById(REJECT_SCHEDULE + call.getId());
    }

    private static String RELEASE_SCHEDULE = "Release-Schedule-";

    public static Object getReleaseScheduleLock(CallService call) {
        return getLockById(RELEASE_SCHEDULE + call.getId());
    }

    /*private static final Map reentrantLocks = new WeakHashMap( 13 );

    private static synchronized ReentrantLock getReentrantLock( final String id ) {
        Object lock = reentrantLocks.get( id );
        if( lock == null ) {
            lock = new ReentrantLock();
            reentrantLocks.put( id, lock );
        }
        return (ReentrantLock)lock;
    }*/

    private static String INVITE_CANCEL = "Invite-Cancel-";

    public static synchronized Object getInviteCancelLock(final SipSession session) {
//        return getReentrantLock( INVITE_CANCEL + session.getId() );
        return getLockById(INVITE_CANCEL + session.getId());
    }


    private static final String PUBLISH_LOCK = "PublishLockFor-";

    public static synchronized Object getPublishLock(UserAgent ua) {
        return getLockById(PUBLISH_LOCK + ua.getSipURIString());
    }

    private static final String SUBSCRIPTION_LOCK = "SubscriptionLock-From-";

    public static synchronized Object getSubscriptionLock(UserAgent subscriber, UserAgent notifier) {
        return getLockById(SUBSCRIPTION_LOCK + subscriber.getSipURIString() + "-To-" + notifier.getSipURIString());
    }


    public static List listLocks() {
        return Arrays.asList(locks.values().toArray());
    }

}