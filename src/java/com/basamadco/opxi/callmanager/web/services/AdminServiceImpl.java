package com.basamadco.opxi.callmanager.web.services;

import com.basamadco.opxi.callmanager.PresenceService;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.pool.AgentPool;
import com.basamadco.opxi.callmanager.pool.rules.AbstractMatchingRule;
import com.basamadco.opxi.callmanager.rule.AbstractRule;
import com.basamadco.opxi.callmanager.sip.presence.BusyEvent;
import com.basamadco.opxi.callmanager.sip.presence.OfflineEvent;
import com.basamadco.opxi.callmanager.sip.presence.BusyPublishEvent;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;
import javax.xml.rpc.server.ServletEndpointContext;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Oct 11, 2006
 *         Time: 9:16:35 AM
 * @web.servlet name="AdminService"
 * service-endpoint-class="com.basamadco.opxi.callmanager.services.AdminService"
 * load-on-startup="1"
 * @web.servlet-mapping url-pattern="/services/AdminService"
 * @wsee.port-component name="AdminService"
 */
public class AdminServiceImpl implements ServiceLifecycle, ApplicationConstants {

    private static final Logger logger = Logger.getLogger( AdminServiceImpl.class.getName() );


    private ServletEndpointContext serviceContext;

    private ServiceFactory serviceFactory;


    public void init( Object context ) throws ServiceException {
        serviceContext = (ServletEndpointContext) context;
        serviceFactory = (ServiceFactory) serviceContext.getServletContext().getAttribute( SERVICE_FACTORY );
        logger.finest( "Init service endpoint: " + serviceContext );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void createAgentProfile( String dn ) throws Exception {
//        OpxiCMEntityProfile profile = getServiceFactory().getProfileService().defaultAgentProfile();
        OpxiCMEntityProfile profile = serviceFactory.getProfileService().defaultAgentProfile();
        profile.getOpxiCMEntityProfileChoice().getAgentProfile().setDN( dn );
        serviceFactory.getProfileService().createAgentProfile( profile );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void updateAgentProfile( String xmlProfile, String contentType, byte[] greetingAudio ) throws Exception {
//        logger.finest("************************************ update started...");
        OpxiCMEntityProfile profile = OpxiCMEntityProfile.unmarshal( new StringReader( xmlProfile ) );
//        logger.finest("************************************ update med 1...");
        serviceFactory.getProfileService().updateAgentProfile( profile, contentType, greetingAudio );
//        logger.finest("************************************ update finished...");
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String readAgentProfile( String dn ) throws Exception {
        StringWriter sw = new StringWriter();
        serviceFactory.getProfileService().readAgentProfile( dn ).marshal( sw );
        return sw.toString();
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void createSkillProfile( String skillDN ) throws Exception {
        OpxiCMEntityProfile profile = serviceFactory.getProfileService().defaultSkillProfile();
        profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().setDN( skillDN );
        serviceFactory.getProfileService().createPoolTargetProfile( profile );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void updatePoolProfile( String xmlPoolProfile, String contentType, byte[] waitingFile ) throws Exception {
        OpxiCMEntityProfile profile = OpxiCMEntityProfile.unmarshal( new StringReader( xmlPoolProfile ) );
        serviceFactory.getProfileService().updatePoolTargetProfile( profile, contentType, waitingFile );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String readPoolProfile( String skillOrWorkgroupDN ) throws Exception {
        StringWriter sw = new StringWriter();
        serviceFactory.getProfileService().readPoolTargetProfile( skillOrWorkgroupDN ).marshal( sw );
        return sw.toString();
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void createWorkgroupProfile( String workgroupDN ) throws Exception {
        OpxiCMEntityProfile profile = serviceFactory.getProfileService().defaultWorkgroupProfile();
        profile.getOpxiCMEntityProfileChoice().getPoolTargetProfile().setDN( workgroupDN );
        serviceFactory.getProfileService().createPoolTargetProfile( profile );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void deleteAgentProfile( String dn ) throws Exception {
        serviceFactory.getProfileService().deleteAgentProfile( dn );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void deletePoolProfile( String dn ) throws Exception {
        serviceFactory.getProfileService().deletePoolTargetProfile( dn );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public void removeProfileAttachment( String dn ) throws Exception {
        serviceFactory.getProfileService().removeAttachment( dn );
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String[] getPoolTypeImplementations() throws Exception {
        Class[] poolTypes = OpxiToolBox.getConcreteSubClasses( AgentPool.class );
        String[] names = new String[poolTypes.length];
        for ( int i = 0; i < poolTypes.length; i++ ) {
            names[i] = poolTypes[i].getName();
        }
        return names;
    }

    /**
     * Return all available concrete subclasses of AbstractMatchingRule (in the same package)
     * with their writeable properties.
     *
     * @return A two dimensional String array with Class names in 0 index and the field names
     *         in next indexes of each row.
     * @web.interface-method
     * @ejb.interface-method
     */
    public String[][] getMatchingRuleImplementations() throws Exception {
        String[][] beans;
        Class[] classes = OpxiToolBox.getConcreteSubClasses( AbstractMatchingRule.class );
        beans = new String[classes.length][];
        for ( int i = 0; i < classes.length; i++ ) {
            Field[] fields = OpxiToolBox.getBeanProperties( classes[i] );
            beans[i] = new String[fields.length + 1];
            beans[i][0] = classes[i].getName();
            for ( int j = 0; j < fields.length; j++ ) {
                beans[i][j + 1] = fields[j].getName();
            }
        }
        return beans;
    }


    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String[] getRuleTargetServices() throws Exception {
        return new String[]{PresenceService.class.getName()};
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String[][] getRuleNames() throws Exception {
        String[][] beans;
        Class[] classes = OpxiToolBox.getConcreteSubClasses( AbstractRule.class );
        beans = new String[classes.length][];
        for ( int i = 0; i < classes.length; i++ ) {
            Field[] fields = OpxiToolBox.getBeanProperties( classes[i] );
            beans[i] = new String[fields.length + 1];
            beans[i][0] = classes[i].getName();
            for ( int j = 0; j < fields.length; j++ ) {
                beans[i][j + 1] = fields[j].getName();
            }
        }
        return beans;
    }


    private static final String[] EVENTS = {BusyPublishEvent.class.getName(), OfflineEvent.class.getName()};

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String[] getRuleEvents() throws Exception {
        return EVENTS;
    }

    private static final String[] ROLES = {"Required", "Teardown"};

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String[] getAppIntRoleNames() {
        return ROLES;
    }


    private static final String[] PARTIES = {
            Leg.CALLER, Leg.AGENT, Leg.TRANSFERED_TO_GREETING,
            Leg.GREETING_MEDIA, Leg.WAITING_MEDIA, Leg.VOICE_APP
    };

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public String[] getAppIntPartyNames() {
        return PARTIES;
    }

    /**
     * @web.interface-method
     * @ejb.interface-method
     */
    public HelloBean testHelloBean() {
        return new HelloBean( "behrad" );
    }


    public void destroy() {
        logger.finest( "Destroy service endpoint: " + serviceContext );
    }
}