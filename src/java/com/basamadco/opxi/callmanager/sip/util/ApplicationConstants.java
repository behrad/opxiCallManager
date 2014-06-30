package com.basamadco.opxi.callmanager.sip.util;

import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

/**
 * Defines some of useful constants by OpxiCallManager
 * sip server application.
 *
 * @author Jrad
 */
public interface ApplicationConstants extends SIPConstants {

    public static final String SERVICE_FACTORY = "com.basamadco.opxi.callmanager.ServiceFactory";

    public final static String DOMAIN = OpxiToolBox.getLocalDomain();

    public final static String LOCAL_USER = PropertyUtil.getProperty( "opxi.callmanager.username" );

    public static final String CALL_MANAGER_URI = "sip:" + LOCAL_USER + "@" + DOMAIN;

    public static final String UTF8 = "UTF8";

    public static final String PROXY_SERVLET = "proxy";

    public static final String QUEUE_SERVLET = "queue";

    public static final String TRANSFER_BY_REFER = "transferByRefer";

    public static final String TRANSFER_BY_REINVITE = "transferByReInvite";

    public static final String AGENT_SERVLET = "agent";

    public static final String GREETING_SERVLET = "greetingServlet";

    public static final String MEDIA_SERVLET = "mediaServlet";


    public static final String B2B_VOICEAPP_SERVLET = "B2BVoiceAppServlet";


    public static final String AGENT_PRESENCE = "agentPresence";

    public static final String CONNECT_SERVLET = "connect";

    public static final String QUEUED_SERVLET = "queued";

    public static final String WAITING_MSG_MACHINE = "waiting";

    public static final String POST_GREETING_SERVLET = "postGreetingServlet";

    public static final String RINGING_APPLICATION_MACHINE = "ringingApplication";

    public static final String OUTBOUND_B2BUA_LEG = "outboundLeg";
}
