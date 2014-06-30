package com.basamadco.opxi.callmanager;

import java.util.List;

/**
 * Common Interface of OPXi Call Manager services available in different modules.
 *
 * A service will belong to a ServiceFactory through which it can access other services.
 *
 * @author Jrad
 *         Date: Apr 6, 2006
 *         Time: 12:07:07 PM
 */
public interface CallManagerServiceInterface {

    /**
     * Sets the factory which this service belongs to
     * @param factory ServiceFactory object which this service belongs to
     */
    public void setServiceFactory( ServiceFactory factory );

    /**
     * Returns the factory which this service belongs to
     * NOTE: This method can be used to access other services available in the factory.
     *
     * @return ServiceFactory object which this service belongs to
     */
    public ServiceFactory getServiceFactory();

    /**
     * A usefull debugging operation for Call Manager monitoring console to extract objects
     * available in memory of each service
     * @return
     */
    public List listObjects();

    /**
     * Let's the service to clean up it's resources
     */
    public void destroy();

}
