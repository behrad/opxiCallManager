package com.basamadco.opxi.callmanager.call.route;

import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;

import javax.servlet.sip.SipServletRequest;

/**
 * Defines Opxi Call Manager's specification for each routing algorithm
 * Routing algorithms will map any incomming call to a required target which
 * is later responsilble to service that call
 * Many different implementations of call routing algorithms may exist in call manager
 * RoutingService is responsible to propogate call route request among prioritized routing
 * algorithms. 
 *
 * @author Jrad
 *         Date: Nov 19, 2007
 *         Time: 10:17:31 AM
 */
public abstract class CallRouter {

    private ServiceFactory sf;

    /**
     * This method should implement the algorithm to find a target to route the input call to.
     * If it matched any call target, it will instantiate and return the object. Otherwise if
     * will return null so that routing service can forward the route request to another router
     *
     * @param request
     * @return The found CallTarget object, otherwise null to let other routers in the chain to process
     * @throws CallRouteException If you want the whole routing process to stop and propagate and error
     */
    public abstract CallTarget findRouteTarget( SipServletRequest request ) throws CallRouteException;

    /**
     * Sets a ServiceFactory object so that routers can access call manager services  
     * @param serviceFactory
     */
    public void setServiceFactory( ServiceFactory serviceFactory ) {
        sf = serviceFactory;
    }

    /**
     * Provides access to the opxi Call Manager ServiceFactory
     * @return
     */
    protected ServiceFactory getServiceFactory() {
        return sf;
    }

}
