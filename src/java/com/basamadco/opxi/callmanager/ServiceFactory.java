package com.basamadco.opxi.callmanager;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import com.basamadco.opxi.callmanager.call.route.RoutingService;
import com.basamadco.opxi.callmanager.crm.CRMService;
import com.basamadco.opxi.callmanager.logging.LogService;
import com.basamadco.opxi.callmanager.pool.AgentService;
import com.basamadco.opxi.callmanager.pool.PoolService;
import com.basamadco.opxi.callmanager.profile.ProfileService;
import com.basamadco.opxi.callmanager.queue.QueueManagementService;
import com.basamadco.opxi.callmanager.sip.registrar.SipRegistrar;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A factory for OPXi Call Manager service implementations.
 * For any service to be accessible in the Call Manager run time,
 * it's ID and corresponding getter/setter methods should be
 * <b>statically</b> added to this class.
 *
 * @author Jrad
 *         Date: Apr 6, 2006
 *         Time: 12:55:16 PM
 * @see com.basamadco.opxi.callmanager.sip.CallManagerAppLoader
 */
public class ServiceFactory {

    private static final Logger logger = Logger.getLogger(ServiceFactory.class.getName());

    /**
     * The attribute name under which opxiCallManager LocationService is set
     * in the ServletContext.
     */
    protected static final String LOCATION_SERVICE = LocationService.class.getName();

    /**
     * The attribute name under which opxiCallManager PresenceService
     * is set in the ServletContext.
     */
    protected static final String PRESENCE_SERVICE = PresenceService.class.getName();

    /**
     * Media Service ID
     */
    protected static final String MEDIA_SERVICE = MediaService.class.getName();

    /**
     * Pool Service ID
     */
    protected static final String POOL_SERVICE = PoolService.class.getName();

    /**
     * Queue Service ID
     */
    protected static final String QUEUE_SERVICE = QueueManagementService.class.getName();

    /**
     * Log Service ID
     */
    protected static final String LOG_SERVICE = LogService.class.getName();

    /**
     * Sip Service ID
     */
    protected static final String SIP_SERVICE = SipService.class.getName();

    /**
     * CRM Service ID
     */
    protected static final String CRM_SERVICE = CRMService.class.getName();

    /**
     * Profile Service ID
     */
    protected static final String PROFILE_SERVICE = ProfileService.class.getName();



    protected static final String ROUTING_SERVICE = RoutingService.class.getName();

    /**
     * Agent Service ID
     */
    protected static final String AGENT_SERVICE = AgentService.class.getName();


    protected static final String REGISTRAR_SERVICE = SipRegistrar.class.getName();

    /**
     * TimerService ID
     */
//    protected static final String TIMER_SERVICE = "TimerService";


    protected static final String[] SERVICE_NAMES = {
            LOCATION_SERVICE, PRESENCE_SERVICE, MEDIA_SERVICE,
            POOL_SERVICE, QUEUE_SERVICE, LOG_SERVICE, CRM_SERVICE, PROFILE_SERVICE, ROUTING_SERVICE, AGENT_SERVICE
    };


    private final Map services = new ConcurrentHashMap();


    public CallManagerServiceInterface getServiceByName( String service_name ) {
        return (CallManagerServiceInterface)services.get(service_name);
    }

    private void setServiceForName(String service_name, CallManagerServiceInterface service) {
        CallManagerServiceInterface oldService = getServiceByName(service_name);
        if (oldService != null) {
            try {
                logger.info("Service " + service_name + " reinitialized. ");
                oldService.destroy();
            } catch (Throwable e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        services.put(service_name, service);
        service.setServiceFactory(this);
    }

    public LocationService getLocationService() {
        return (LocationService) getServiceByName(LOCATION_SERVICE);
    }

    public void setLocationService(LocationService ls) {
        setServiceForName(LOCATION_SERVICE, ls);
    }

    public PresenceService getPresenceService() {
        return (PresenceService) getServiceByName(PRESENCE_SERVICE);
    }

    public void setPresenceService(PresenceService ps) {
        setServiceForName(PRESENCE_SERVICE, ps);
    }

    public CRMService getCRMService() {
        return (CRMService) getServiceByName(CRM_SERVICE);
    }

    public void setCRMService(CRMService crm) {
        setServiceForName(CRM_SERVICE, crm);
    }

    public MediaService getMediaService() {
        return (MediaService) getServiceByName(MEDIA_SERVICE);
    }

    public void setMediaService(MediaService ms) {
        setServiceForName(MEDIA_SERVICE, ms);
    }

    public PoolService getPoolService() {
        return (PoolService) getServiceByName(POOL_SERVICE);
    }

    public void setPoolService(PoolService service) {
        setServiceForName(POOL_SERVICE, service);
    }

    public QueueManagementService getQueueManagementService() {
        return (QueueManagementService) getServiceByName(QUEUE_SERVICE);
    }

    public void setQueueManagementService(QueueManagementService service) {
        setServiceForName(QUEUE_SERVICE, service);
    }

    public LogService getLogService() {
        return (LogService) getServiceByName(LOG_SERVICE);
    }

    public void setLogService(LogService logService) {
        setServiceForName(LOG_SERVICE, logService);
    }

    public SipService getSipService() {
        return (SipService) getServiceByName(SIP_SERVICE);
    }

    public void setSipService(SipService sipService) {
        setServiceForName(SIP_SERVICE, sipService);
    }

    public ProfileService getProfileService() {
        return (ProfileService) getServiceByName(PROFILE_SERVICE);
    }

    public void setProfileService(ProfileService service) {
        setServiceForName(PROFILE_SERVICE, service);
    }

    public RoutingService getRoutingService() {
        return (RoutingService) getServiceByName(ROUTING_SERVICE);
    }

    public void setRoutingService(RoutingService service) {
        setServiceForName(ROUTING_SERVICE, service);
    }

    public AgentService getAgentService() {
        return (AgentService) getServiceByName(AGENT_SERVICE);
    }

    public void setAgentService(AgentService as) {
        setServiceForName(AGENT_SERVICE, as);
    }

    public void setRegistrarService( SipRegistrar registrarService ) {
        setServiceForName( REGISTRAR_SERVICE, registrarService );
    }

    public SipRegistrar getRegistrarService() {
        return ( SipRegistrar) getServiceByName( REGISTRAR_SERVICE );
    }

    public List listServices() {
        return Arrays.asList( services.values().toArray() );
    }

    public void destroy() {
        Iterator all = services.values().iterator();
        synchronized (services) {
            while (all.hasNext()) {
                CallManagerServiceInterface service = (CallManagerServiceInterface) all.next();
                service.destroy();
            }
        }
        services.clear();
        logger.info("ServiceFactory destroyed successfully.");
    }


}