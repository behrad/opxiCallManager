package com.basamadco.opxi.callmanager.sip;

import com.basamadco.opxi.callmanager.call.CallService;
import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;
import java.util.logging.Logger;

/**
 * CallServlet does all sip front handling logic in the SipServlet invironment
 * 
 * @author Jrad
 * @since 0.2
 */
public class CallServlet extends OpxiSipServlet {

    private final static Logger logger = Logger.getLogger( CallServlet.class.getName() );



    public static final String GREETING_URI_USER = PropertyUtil.getProperty("opxi.callmanager.greeting.cicso.username");

    protected static URI GREETING_URI = null;

    protected final static String CALL_TERMINATED_EVENT = "OPXICALLTERMINATED";

    protected final static String CALL_TERMINATED_EVENT_USER = "OpxiCallTerminated";

    protected static final String CALL_TRANSFER_EVENT = "OPXICALLTRANSFER";

    protected static final String CALL_TRANSFER_EVENT_USER = "OpxiCallTransfer";

    protected static final String CALL_SCHEDULE_EVENT = "OPXICALLSCHEDULE";

    protected static final String CALL_SCHEDULE_EVENT_USER = "OpxiCallSchedule";

    protected static final String CALL_WAIT_EXCEEDED_EVENT = "WAITTIMEEXCEEDED";

    protected static final String CALLEE_URI_HEADER = "x-Opxi-Callee-URI";

    protected static final String AGENT_URI_HEADER = "x-Opxi-Agent-URI";

    protected static final String SIPCALL_ID_HEADER = "x-Opxi-SipCall-ID";

    protected static final String TRANSFER_TO_HEADER = "x-Opxi-Transfer-To";

    protected static final String REASON_HEADER = "x-Opxi-Reason";

    protected static final String AGENT_URI_PARAM = "agent";

    public static final String TRANSFER_CALLID_PARAM = "opxi-transfereeCallId";

    protected static final String CONNECT_URI_USER = "connect";

    protected static final String TRANSFER_URI = "TransferURI";

    public static final String mode_conf = PropertyUtil.getProperty("opxi.callmanager.transfer.defaultmode");

    public static final String REINVITE_TRANSFER = "reInviteTransfer";

    public static final String REFER_TRANSFER = "referTransfer";

    public static final String TRANSFER_MODE = "opxiTransferMode";

    public static final String MAX_CALL_WAIT_TIME_REACHED = "MaxCallWaitTimeReached";

    public static final String EVENT_NAME = "EventName";


    protected final URI getGreetingURI() throws ServletException {
        if( GREETING_URI == null ) {
            GREETING_URI = getSipFactory().createSipURI( GREETING_URI_USER, DOMAIN );
        }
        return GREETING_URI;
    }

    /*protected final void doQueue( SipServletRequest request ) throws ServletException, IOException {
//        logger.severe( "$$$$$ before request '"+request.getCallId()+"' forward: " + System.currentTimeMillis() );
        forward( request, QUEUE_SERVLET );
//        logger.severe( "$$$$$ after request '"+request.getCallId()+"' forward: " + System.currentTimeMillis() );
    }

    protected final void doGreeting( SipServletRequest request ) throws ServletException, IOException {
//        forward( request, AGENT_SERVLET );
        forward( request, GREETING_SERVLET );
    }


    protected final void doWait( SipServletRequest request ) throws ServletException, IOException {
        forward( request, QUEUED_SERVLET );
    }

    protected final void doPostGreeting( SipServletRequest request ) throws ServletException, IOException {
        forward( request, POST_GREETING_SERVLET );
    }*/


    protected Leg getLeg( SipSession session ) {
        Object attr = null;
        try {
            attr = session.getAttribute( Leg.class.getName() );
        } catch ( IllegalStateException e ) {
            logger.severe( "SipSession is already invalidated: " + e.getMessage() );
            throw e;
        }
        if( attr == null ) {
            throw new NoLegAttributeIsSetException( session.getCallId() );
        }
        return (Leg)attr;

    }

    protected final CallService getCallService( SipSession session ) {
        return getLeg( session ).getCallService();
    }

}