package com.basamadco.opxi.callmanager.sip.listener;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.callmanager.util.LockManager;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionEvent;
import javax.servlet.sip.SipSessionListener;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages SipSessions lifecycle to 
 * @author Jrad
 *         Date: Aug 29, 2006
 *         Time: 5:12:17 PM
 */
public class SipSessionManager implements SipSessionListener {

    private static final Logger logger = Logger.getLogger( SipSessionManager.class.getName() );


    private static final int CONCURRENT_SIP_SESSION_SIZE = Integer.parseInt(
            PropertyUtil.getProperty( "opxi.callmanager.sip.concurrentSipSessions" )
    );

    private static final Map sipSessionMap = new ConcurrentHashMap( CONCURRENT_SIP_SESSION_SIZE );

    
    private static final String SIP_SESSION_MANAGER_LOCK_ID = "SipSessionManager-";


    public void sessionCreated( SipSessionEvent sipSessionCreationEvent ) {
        SipSession newSession = sipSessionCreationEvent.getSession();
        logger.finest( "SipSession " + newSession + " with callId '" + newSession.getCallId() + "' created" );
        /*synchronized( LockManager.getLockById( SIP_SESSION_MANAGER_LOCK_ID + newSession.getCallId() ) ) {
            sipSessionMap.put( newSession.getCallId(), newSession );
        }*/
    }

    public void sessionDestroyed( SipSessionEvent sipSessionDestroyedEvent ) {
        SipSession oldSession = sipSessionDestroyedEvent.getSession();
        logger.finest( "SipSession " + oldSession + " with callId '" + oldSession.getCallId() + "' destroyed" );
        /*synchronized( LockManager.getLockById( SIP_SESSION_MANAGER_LOCK_ID + oldSession.getCallId() ) ) {
            sipSessionMap.remove( oldSession.getCallId() );
        }*/
    }

    public static void add( SipSession session ) {
        synchronized( LockManager.getLockById( SIP_SESSION_MANAGER_LOCK_ID + session.getCallId() ) ) {
            sipSessionMap.put( session.getCallId(), session );
            logger.finest( "SipSession for '" + session.getCallId() + "' added to map" );
        }
    }

    public static void remove( SipSession session ) {
        synchronized( LockManager.getLockById( SIP_SESSION_MANAGER_LOCK_ID + session.getCallId() ) ) {
            sipSessionMap.remove( session.getCallId() );
            logger.finest( "SipSession for '" + session.getCallId() + "' removed from map" );
        }
    }

    public static SipSession getByCallId( String callId ) {
        synchronized( LockManager.getLockById( SIP_SESSION_MANAGER_LOCK_ID + callId ) ) {
            if( sipSessionMap.containsKey( callId ) ) {
                return (SipSession)sipSessionMap.get( callId );
            } else {
                throw new IllegalStateException( "No SipSession for callId '" + callId +  "' exists." );
            }
        }
    }

    public static List listSessions() {
        Object[] sessions = sipSessionMap.values().toArray();
        List list = new ArrayList();
        for (int i = 0; i < sessions.length; i++) {
            SipSession session = (SipSession)sessions[i];
            list.add( session.getCallId() );
        }
        return list;
    }

    public static void clearSessions() {
//        logger.finer( "Start cleaning up..." );
        Iterator iter = sipSessionMap.values().iterator();
        while ( iter.hasNext()) {
            SipSession s =  (SipSession)iter.next();
            s.getApplicationSession().invalidate();
//            s.invalidate();
        }
        sipSessionMap.clear();
//        logger.finer( "End cleaning up..." );
    }

}
