package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.pool.Agent;
import com.basamadco.opxi.callmanager.pool.AgentNotAvailableException;
import com.basamadco.opxi.callmanager.sip.MediaServerNotRegistered;
import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;
import com.basamadco.opxi.callmanager.sip.registrar.RegistrationNotFoundException;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.URI;
import javax.servlet.sip.Address;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This service type handle all OPXi Media Server stuff.
 * <p/>
 * Currently support for only one running media server in
 * call manager domain is implemented.
 *
 * @author Jrad
 *         Date: Apr 24, 2006
 *         Time: 9:42:22 AM
 */
public class MediaService extends AbstractCallManagerService {

    private static final Logger logger = Logger.getLogger( MediaService.class.getName() );


    public static UserAgent defaultMediaServerUA = new UserAgent( OpxiSipServlet.OPXI_IVR_USER, OpxiSipServlet.DOMAIN );

    private String IVR_MEDIA_URI = PropertyUtil.getProperty( "opxi.callmanager.ivr.mediaURLprefix" );

    private String VXML_APP_URL = PropertyUtil.getProperty( "opxi.vxml.app.url" );

    private String MEDIA_PARAM = PropertyUtil.getProperty( "opxi.vxml.app.media.param" );

    public static final String RING_TONE_URL = PropertyUtil.getProperty( "opxi.callmanager.greeting.ringtoneURL" );

    private IVRNotRegisteredReport ivrReport;

    private Map<Registration, Integer> IVRLoads = new HashMap<Registration, Integer>();


//    private static Registration IVR_CONTACT;

//    private final Map greetingCtxMap = new ConcurrentHashMap( 13 );

//    public MediaService( ServiceFactory factory ) {
//        super( factory );
//    }

    /*private String getMediaURI( Address mediaAddress ) throws MediaServerNotRegistered {
        return getMediaURI( mediaAddress.getURI(), mediaAddress.getParameter( "dialog" ) );
    }*/

    /**
     * Returns the phisycal address to access the specified voice application on
     * the media server service.
     *
     * @param vxmlAppUrl The voice application URL
     * @return The address on the media server to access the voice application
     * @throws OpxiException
     */
    public String getVoiceAppURI( String vxmlAppUrl ) throws OpxiException {
        return getMediaURI( vxmlAppUrl );
    }

    /**
     * Returns waiting media access URI for specified media URI, username and password.
     *
     * @param mediaURI
     * @param user     The username for who the media URI should be accessed
     * @param pass     the user password to access media URI
     * @return The actual address on media server
     * @throws MediaServerNotRegistered
     */
    public URI getWaitingRoomMediaURI( String mediaURI, String user, String pass ) throws MediaServerNotRegistered {
        try {
            return getServiceFactory().getSipService().getSipFactory().createURI(
                    getMediaURI( getWRAppUrl( mediaURI, user, pass ) )
            );
        } catch ( ServletParseException e ) {
            logger.severe( e.getMessage() );
            throw new IllegalStateException( e );
        }
    }

    public URI getWaitingRoomMediaURI( String mediaURI ) throws MediaServerNotRegistered {
        return getWaitingRoomMediaURI( mediaURI, null, null );
    }

    private String getWRAppUrl( String mediaURI, String user, String pass ) {
        if ( user != null ) {
            return VXML_APP_URL + "waitingRoom" + MEDIA_PARAM + /*"\"" +*/ mediaURI /*+ "\""*/
                    + "&u=" + user + "&p=" + pass;
        } else {
            return VXML_APP_URL + "waitingRoom" + MEDIA_PARAM + /*"\"" +*/ mediaURI /*+ "\""*/;
        }
    }

//    public String getGreetingMediaURI( String greetingMedia, String user, String pass,
//                                       String caller, String agentId, String ringtone ) throws MediaServerNotRegistered {
//        return getMediaURI( defaultMediaServerUA, getGreetingVXMLAppUrl( greetingMedia, user, pass, caller, agentId, ringtone ) );
//    }

    public URI getGreetingMediaURI( String callerId, Agent agent, String ringtone ) throws MediaServerNotRegistered {
        try {
            if ( ringtone == null ) {
                ringtone = RING_TONE_URL;
            }
            String user = SipUtil.getName( agent.getAOR() );
            String pass = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getUserPassword( user );

            String uri = getMediaURI( getGreetingVXMLAppUrl( agent.getGreetingMsgURI(),
                    user, pass, callerId, ringtone )
            );
            return getServiceFactory().getSipService().getSipFactory().createURI( uri );
        } catch ( DAOException e ) {
            logger.severe( e.getMessage() );
            throw new IllegalStateException( e );
        } catch ( DAOFactoryException e ) {
            logger.severe( e.getMessage() );
            throw new IllegalStateException( e );
        } catch ( ServletParseException e ) {
            logger.severe( e.getMessage() );
            throw new IllegalStateException( e );
        }
    }

    public URI getGreetingMediaURI( String callerId, Agent agent ) throws MediaServerNotRegistered {
        return getGreetingMediaURI( callerId, agent, null );
    }

    /**
     * Returns the greeting media access uri for specified greeting URI, user and pass
     * in the GreetingContext specified by ctxId and with ringtone playback ringtone.
     *
     * @param greeting The greeting media URI
     * @param user     the user name who greeting media will be accessed for
     * @param pass     the user password to access the greeting media
     * @param ctxId    GreetingContext id on which this greeting message will be played for
     * @param ringtone ring tone playback
     * @return The actual greeting media address
     */
    private String getGreetingVXMLAppUrl( String greeting, String user, String pass, String ctxId, String ringtone ) {
        return VXML_APP_URL + "greeting" + MEDIA_PARAM + /*"\"" +*/ greeting /*+ "\""*/
                + "&u=" + user + "&p=" + pass + "&greeting_ctxId=" + ctxId.replaceAll( "@", "%40" ) + "&ringtone=" + ringtone;
        //greeting_ctx_id
    }

//    private String getVXMLAppUrl( String vxmlApp, String mediaURI, String user, String pass ) {
//        return VXML_APP_URL + vxmlApp + MEDIA_PARAM + /*"\"" +*/ mediaURI /*+ "\""*/
//                + "&u=" + user + "&p=" + pass;
//    }

    /*private String getMediaURI( URI mediaServerAOR, String vxmlAppUrl ) throws MediaServerNotRegistered {
        UserAgent mediaServer = new UserAgent( mediaServerAOR );
        return getMediaURI( mediaServer, vxmlAppUrl );
    }*/

    private String getMediaURI( String vxmlAppUrl ) throws MediaServerNotRegistered {
        try {
            String ivr = getMediaServerContactLocation();
            String host = ivr.split( "@" )[1];
            String msgURI = "sip:" + filter( vxmlAppUrl ) + "@" + host;
            if ( vxmlAppUrl.indexOf( "http" ) > -1 ) {
                if ( vxmlAppUrl.indexOf( IVR_MEDIA_URI ) < 0 ) {
                    // http without sip:dialog...
                    msgURI = "sip:" + IVR_MEDIA_URI + filter( vxmlAppUrl ) + "@" + host;
                }
            }
            logger.finer( "DIALOG MSG URI: " + msgURI );
            return msgURI;
        } catch ( MediaServerNotRegistered e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            /*getServiceFactory().getSipService().sendAdminIM(
                    ResourceBundleUtil.getMessage("callmanager.admin.ivr.unavailable"));
            getServiceFactory().getSipService().sendAdminIM(
                    ResourceBundleUtil.getMessage("callmanager.admin.ivr.reRegister"));*/
            try {
                getServiceFactory().getLogService().getServiceActivityLogger()
                        .addUnsuccessfulHandling( "Media Server Not Registered",
                                "Please (re)start your media server with AOR 'sip:opxiIVR@cc.basamad.acc'" );
            } catch ( OpxiException e2 ) {
                logger.log( Level.SEVERE, e2.getMessage(), e2 );
            }
            throw e;
        }
    }

    private synchronized String getMediaServerContactLocation() throws MediaServerNotRegistered {
        try {
            try {
                String ivrURI = selectMediaServer().getLocation().getURI().toString();
                checkDisableReport();
                return ivrURI;
            } catch ( RegistrationNotFoundException e ) {
                throw new MediaServerNotRegistered( "Media server is not registered" );
            }
        } catch ( MediaServerNotRegistered mediaServerNotRegistered ) {
            checkGenerateReport();
            throw mediaServerNotRegistered;
        }
    }

    private Registration selectMediaServer() throws RegistrationNotFoundException {
        int minConnections = 0x0fffffff;
        Registration selected = null;
        for ( Registration ivr : getServiceFactory().getLocationService().findRegistrations( defaultMediaServerUA ) ) {
            if ( IVRLoads.get( ivr ) == null ) {
                IVRLoads.put( ivr, 0 );
            }
            if ( IVRLoads.get( ivr ) < minConnections ) {
                minConnections = IVRLoads.get( ivr );
                selected = ivr;
            }
        }
        if ( selected == null ) {
            throw new RegistrationNotFoundException( "No IVR server could be selected!" );
        }
        serverUpdate( selected, minConnections + 1 );
        return selected;
    }


    /**
     * No need to synchronize this method with selectServer since we don't want real-time load decision
     *
     * @param contact
     * @param aliveCalls
     */
    public void serverUpdate( Registration contact, int aliveCalls ) {
        IVRLoads.put( contact, aliveCalls );
    }


    private void checkDisableReport() {
        if ( ivrReport != null ) {
            ivrReport.cancel();
            ivrReport = null;
        }
    }

    private void checkGenerateReport() {
        if ( ivrReport == null ) {
            ivrReport = new IVRNotRegisteredReport( getServiceFactory(), defaultMediaServerUA.getAORString() );
        }
    }

    private String filter( String vxmlAppUrl ) {
        return vxmlAppUrl.replaceAll( ":", "%3a" );
    }

    public List listObjects() {
//        return Arrays.asList( greetingCtxMap.values().toArray() );
        throw new IllegalStateException( "No memory resources available for this service" );
    }

    public void destroy() {
//        greetingCtxMap.clear();
        logger.info( "MediaService destroyed successfully." );
    }

}