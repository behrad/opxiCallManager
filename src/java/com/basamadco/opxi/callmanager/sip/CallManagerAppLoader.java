package com.basamadco.opxi.callmanager.sip;

import com.basamadco.opxi.callmanager.*;
import com.basamadco.opxi.callmanager.call.route.RoutingService;
import com.basamadco.opxi.callmanager.crm.CentricCRMService;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.dao.webdav.StorageDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.Storage;
import com.basamadco.opxi.callmanager.logging.LogService;
import com.basamadco.opxi.callmanager.logging.MemoryLogManager;
import com.basamadco.opxi.callmanager.pool.Agent;
import com.basamadco.opxi.callmanager.pool.AgentService;
import com.basamadco.opxi.callmanager.pool.PoolService;
import com.basamadco.opxi.callmanager.profile.WebdavProfileService;
import com.basamadco.opxi.callmanager.profile.CouchProfileService;
import com.basamadco.opxi.callmanager.queue.QueueManagementService;
import com.basamadco.opxi.callmanager.sip.registrar.SipRegistrar;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OPXi Call Manager application startup entry point. Instantiates application services
 * and populates them into ServiceFactory.
 *
 * @author Jrad
 */
public class CallManagerAppLoader extends OpxiSipServlet {

    private static final Logger logger = Logger.getLogger( CallManagerAppLoader.class.getName() );


    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        context.setAttribute( SERVICE_FACTORY, new ServiceFactory() );
        try {
            /**
             * SipService should be initialized first, as other services may need it to initialize
             */
            getServiceFactory().setSipService( new SipService(
                    getServletContext().getServletContextName(),
                    getSipFactory(),
                    getTimerService() )
            );

            getServiceFactory().setLocationService( new LocationService( getServiceFactory() ) );

            getServiceFactory().setPresenceService( new PresenceService() );

            getServiceFactory().setRegistrarService( new SipRegistrar( getServiceFactory() ) );

            getServiceFactory().setQueueManagementService( new QueueManagementService() );

            getServiceFactory().setAgentService( new AgentService() );

            getServiceFactory().setPoolService( new PoolService() );

            getServiceFactory().setMediaService( new MediaService() );

            getServiceFactory().setLogService( new LogService( getServiceFactory(), new MemoryLogManager() ) );

            getServiceFactory().setCRMService( new CentricCRMService() );

            if ( StorageDAOFactory.STORAGE_TYPE == Storage.exchange ) {
                getServiceFactory().setProfileService( new WebdavProfileService() );
            } else {
                getServiceFactory().setProfileService( new CouchProfileService() );
            }

            RoutingService RS = new RoutingService();
            getServiceFactory().setRoutingService( RS );
            RS.initialize();

            logger.info( "Opxi Call Manager started successfully. :)" );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            logger.severe( "Opxi Call Manager couldn't start successfully :( '" + e.getMessage() + "'" );
        }
    }


    public final void reloadPools() throws ServletException {
//        getServiceFactory().getPoolService().destroy();
        getServiceFactory().setPoolService( new PoolService() );
    }


    public final void reloadQueues() throws ServletException {
//        getServiceFactory().getQueueManagementService().destroy();
        getServiceFactory().setQueueManagementService( new QueueManagementService() );
    }

    public void destroy() {
        try {

            for ( Registration contact : getServiceFactory().getLocationService().getAllRegistrations() ) {
                getServiceFactory().getSipService().sendIM( contact,
                        ResourceBundleUtil.getMessage( "callmanager.server.restart" ) );
                getServiceFactory().getSipService().sendIM( contact,
                        ResourceBundleUtil.getMessage( "callmanager.server.restart.relogin" ) );
            }
            getServiceFactory().destroy();
            logger.info( "Opxi Call Manager stopped successfully. :)" );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            logger.severe( "Opxi Call Manager didn't stop successfully. :(" );
        }
    }

}