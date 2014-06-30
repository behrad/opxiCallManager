package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;

import javax.servlet.sip.SipURI;
import java.util.logging.Logger;

import java.util.*;

/**
 * A simple Object Cache Manager for SipCalls
 *
 * @author Jrad
 *         Date: Feb 6, 2006
 *         Time: 8:17:25 PM
 */
public abstract class CallServiceFactory {

    private static final Logger logger = Logger.getLogger( CallServiceFactory.class.getName() );

//    private static Map callMap = Collections.synchronizedMap( new HashMap() );

    private static final Map<String, CallService> callMap;

    static {
        int size = 100;
        try {
            size = Integer.parseInt( PropertyUtil.getProperty( "opxi.callmanager.defaultConcurrentCallsSize" ) );
        } catch ( NumberFormatException e ) {
            logger.severe( "Define 'opxi.callmanager.defaultConcurrentCallsSize' property as a non-negative Integer please" );
        } finally {
            callMap = new java.util.concurrent.ConcurrentHashMap<String, CallService>( size );
        }
    }


//    private static Map callMap = new MultiHashMap();

    public static CallService getCallService( String callId ) throws CallNotExistsException {
        if ( callMap.containsKey( callId ) )
            return (CallService) callMap.get( callId );
        // give them more chance! callId may be a non-caller leg.
//        return legBasedCallServiceFind( callId );
        throw new CallNotExistsException( callId );
    }

    public static CallService getCallByCallerId( String callerURI ) throws CallNotExistsException {
        for ( CallService call : callMap.values() ) {
            SipURI uri = (SipURI) call.getCallerAddress().getURI().clone();
            if ( callerURI.equals( SipUtil.toSipURIString( uri.getUser(), uri.getHost() ) ) ) {
                return call;
            }
        }
        throw new CallNotExistsException( "No Call found for callerId '" + callerURI + "'" );
    }
//    public static CallService createCallService( SipServletRequest request, SipService ctx ) {
//        CallService front = null;
//        try {
//            front = getCallService( request.getCallId() );
//        } catch ( CallNotExistsException e ) {
//            front = new CallService( request, ctx );
//        }
//        callMap.put( request.getCallId(), front );
//        return front;
//    }

    public static void removeCallIndex( String callId ) {
        if ( callId != null ) {
            callMap.remove( callId );
//            front.dispose();
        } else {
            throw new IllegalStateException( "Null callId to remove!" );
        }
    }


    public static void createCallIndex( String callId, CallService call ) {
        if ( !callMap.containsKey( callId ) ) {
            callMap.put( callId, call );
        } else {
            throw new IllegalStateException( "CallId '" + callId + "' is already assigned to call '"
                    + callMap.get( callId ) + "'"
            );
        }
    }

    public static Collection callList() {
        Collection calls = new ArrayList( callMap.values() );
        return Collections.unmodifiableCollection( calls );
    }

}
