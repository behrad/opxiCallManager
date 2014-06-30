package com.basamadco.opxi.callmanager.call.route;

import com.basamadco.opxi.callmanager.DirectoryCallManagerService;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.call.CallTarget;

import javax.servlet.sip.SipServletRequest;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Maintains a run-time chain of CallRouter implementations with specific priorities. When a call route request
 * comes in, forwards it in the CallRouter chain untill an implementation returns a CallTarget object
 *
 * @author Jrad
 *         Date: Nov 19, 2007
 *         Time: 10:22:50 AM
 */
public class RoutingService extends DirectoryCallManagerService {

    private static final Logger logger = Logger.getLogger( RoutingService.class.getName() );

    private final static String routingRuleKeyBase = "opxi.callmanager.routing.ruleItem.";

    /**
     * We'll be sure that routers are sorted in priority order by using a TreeMap with <priority, router> pairs
     */
    private final Map<Integer, CallRouter> routersMap = new TreeMap<Integer, CallRouter>();


    public RoutingService() {
    }

    public void initialize() {
        buildRoutersMap();
    }

    private void buildRoutersMap() {
        int i = 1;
        String ruleItem = PropertyUtil.getProperty( routingRuleKeyBase + i );
        while (ruleItem != null) {
            try {
                addRouterByImplementation( Class.forName( ruleItem ), i );
            } catch ( Exception e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
                logger.finest( "Trying next rule..." );
            }
            ruleItem = PropertyUtil.getProperty( routingRuleKeyBase + ++i );
        }
    }

    /**
     * Should be synchronized with route method
     *
     * @param routerImplClass
     * @param priority
     * @throws CallRouteException
     */
    public void addRouterByImplementation( Class routerImplClass, int priority ) throws CallRouteException {
        try {
            Object o = routerImplClass.newInstance();
            if (o instanceof CallRouter) {
                CallRouter routerImpl = (CallRouter) o;
                routerImpl.setServiceFactory( getServiceFactory() );
                addRouter( routerImpl, priority );
            } else {
                throw new CallRouteException( "Invalid implementation type: '" + routerImplClass + "' should be" +
                        " an implementation of com.basamadco.opxi.callmanager.call.route.CallRouter" );
            }
        } catch ( InstantiationException e ) {
            logger.severe( e.getMessage() );
            throw new CallRouteException( e );
        } catch ( IllegalAccessException e ) {
            logger.severe( e.getMessage() );
            throw new CallRouteException( e );
        }
    }

    /**
     * Should be synchronized with route method
     *
     * @param router
     * @param priority
     */
    private void addRouter( CallRouter router, int priority ) {
        routersMap.put( priority, router );
    }

    /**
     * Implement so that cocurrent calls of this method can be served at the same time
     *
     * @param request
     * @return the CallTarget object found by a call router. This method never returns null.
     * @throws CallRouteException if no router matched to find a target for the call or an error happens
     */
    public CallTarget route( SipServletRequest request ) throws CallRouteException {
        Object[] routers = routersMap.values().toArray(); // copies values to a new array for concurrent access
        for (int i = 0; i < routers.length; i++) {
            CallTarget target = ( (CallRouter) routers[i] ).findRouteTarget( request );
            if (target != null) {
                target.setServiceFactory( getServiceFactory() );
                target.setRequest( request );
                return target;
            }
        }
        // so none of the routers worked to find a call target?
        throw new CallRouteMatchingException( "No router matched to route call with id '" + request.getCallId() + "'" );
    }

    public List listObjects() {
        List<String> out = new ArrayList<String>();
        for( int priority : routersMap.keySet() ) {
            out.add( "Rule " + priority + ": " + routersMap.get( priority ).toString() );
        }
        return out;
    }

    public void destroy() {
        routersMap.clear();
    }
}
