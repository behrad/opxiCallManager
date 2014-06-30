package com.basamadco.opxi.callmanager.sip;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.ServiceFactory;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.sip.registrar.NoAuthorizationHeaderAvailable;
import com.basamadco.opxi.callmanager.sip.security.InvalidBindException;
import com.basamadco.opxi.callmanager.sip.security.SIPDigestMD5Server;
import com.basamadco.opxi.callmanager.sip.util.ApplicationConstants;
import com.basamadco.opxi.callmanager.util.LockManager;
import com.basamadco.opxi.callmanager.util.PropertyUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OpxiCallManager Controller SipServlet.<br>
 * This servlet is common servlet environment for all
 * opxi sipservlets and implements some handy operations.<br>
 * <p/>
 * OpxiSipServlet also seems as a service factory for underlying
 * opxiCallManager subservices.
 *
 * @author Jrad
 * @version 1.0
 */
public class OpxiSipServlet extends SipServlet implements ApplicationConstants {

    /**
     * Log4J logger object used by opxi sipservlets to logg.
     */
    private static final Logger logger = Logger.getLogger( OpxiSipServlet.class.getName() );

    /**
     * If opxiCallManager should ignore security options.<br> This option
     * is set according to opxi.ignore.authentication property.
     */
    private static final boolean SIP_ATHENTICATION_ENABLED =
            Boolean.valueOf( PropertyUtil.getProperty( "opxi.callmanager.sip.auth.enable" ) ).booleanValue();

    /**
     * Attribute name under witch DispatchContext is stored in a sip session
     */
    private static final String DISPATCH_CONTEXT = "com.basamad.opxi.sip.DispatchContext";


    protected static final String OPXI_PRESENCE_EVENT = "OPXIPRESENCE";


    protected static final String PRESENCE_EVENT_USER = "OpxiPresence";


    protected final static String UA_ID_HEADER = "Opxi-PresenceId";


    protected final static String IS_EVENT_MESSAGE = "event-msg";


    /**
     * OpxiCallManager proxy supervised configuration under property name "opxi.proxy.supervised"
     */
    public static final boolean SUPERVISED = Boolean.valueOf( PropertyUtil.getProperty( "opxi.callmanager.sip.proxy.supervised" ) ).booleanValue();

    /**
     * OpxiCallManager proxy record routing config under property name "opxi.proxy.record_route"
     */
    public static final boolean RECORD_ROUTE = Boolean.valueOf( PropertyUtil.getProperty( "opxi.callmanager.sip.proxy.record_route" ) ).booleanValue();

    /**
     * OpxiCallManager proxy transactional state under property name "opxi.proxy.statefull"
     */
    private static final boolean STATEFUL = Boolean.valueOf( PropertyUtil.getProperty( "opxi.callmanager.sip.proxy.stateful" ) ).booleanValue();


    public static final String OPXI_IVR_USER = PropertyUtil.getProperty( "opxi.callmanager.ivr.username" );


    private static ServletContext ctx;

//    protected static SipService sipCtx;

    /**
     * OpxiSipServlet initializer.<br>
     * Makes context and sipFactory objects exists to all
     * opxi sipservlets.
     */
    public void init() throws ServletException {
        if (ctx == null) {
            ctx = super.getServletContext();
        }
    }

    public ServletContext getServletContext() {
        return ctx;
    }

//    public SipService getSipContext() {
//        return sipCtx;
//    }

    protected void doRequest( SipServletRequest request ) throws ServletException, IOException {
        synchronized (LockManager.getSessionLock( request.getSession() )) {
//            logger.finer( "event '" + request.getCallId() + "' receive timestamp: " + System.currentTimeMillis() );
            logger.finer(
                    "Receive Request " + request.getMethod() + " with Call-ID = " + request.getCallId() + " by " + getServletName()
//                    + "\r\n"
//                    + request
            );

//            logger.finest( "*************************** SipSession object: " + request.getSession() );
//            logger.finest( "*************************** AppSession object: " + request.getApplicationSession() );

            /* Since call manager is responsible for managing SipApplicationSession life-cycles,
             * we just wanna ensure that there is created a SipApplicationSession for every sip session.
             */
            request.getApplicationSession( true );

            //        if( APPLICATION_SESSION == null ) { // what if it is invalidated ?!
            //            APPLICATION_SESSION = request.getApplicationSession( true );
            //        }
            try {

                if (request.getHeader( "Timestamp" ) != null)
                    request.removeHeader( "Timestamp" );

                if (request.getAttribute( IS_EVENT_MESSAGE ) != null) {
//                    logger.finer( "Is internal event-request-forward: TRUE" );
                    doEvent( request );
                } else {
                    if (SIP_ATHENTICATION_ENABLED) {
                        if (toValidate( request )) {
                            validate( request );
                        }
                    }
                    if (request.getMethod().equals( REFER )) {
                        doRefer( request );
                        /*if( !request.isInitial() ) {
                            forwardInSession( request );
                        }*/
                        return;
                    } else if (request.getMethod().equals( PUBLISH )) {
                        doPublish( request );
                        /*if( !request.isInitial() ) {
                            forwardInSession( request );
                        }*/
                        return;
                    } else {
                        if (request.getMethod().equals( INVITE ) || request.getMethod().equals( CANCEL )) {
                            synchronized (LockManager.getInviteCancelLock( request.getSession() )) {
                                super.doRequest( request );
                                /*if( !request.isInitial() ) {
                                    forwardInSession( request );
                                }*/
                                return;
                            }
                        } else {
                            super.doRequest( request );
                            /*if( !request.isInitial() ) {
                                forwardInSession( request );
                            }*/
                            return;
                        }
                    }

                }
            } catch ( NoAuthorizationHeaderAvailable e ) {
                logger.finer( e.getMessage() );
                create401Challenge( request ).send();
            } catch ( InvalidBindException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
                request.createResponse( SipServletResponse.SC_FORBIDDEN ).send();
            } catch ( OpxiException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
                sendErrorResponse( request, e );
            }
        }
    }

    protected void doResponse( SipServletResponse response ) throws ServletException, IOException {
        synchronized (LockManager.getSessionLock( response.getSession() )) {
            logger.finer(
                    "Receive Response " + response.getStatus() + "/" + response.getMethod()
                            + " " + response.getCallId() + " by " + getServletName()
//                    + "\r\n"
//                    + response
            );
            super.doResponse( response );
//            forwardInSession( response );
        }
    }

    protected void doInvite( SipServletRequest sipServletRequest ) throws ServletException, IOException {
        try {
            if (sipServletRequest.isInitial()) {
                doInitialInvite( sipServletRequest );
            } else {
                doReInvite( sipServletRequest );
//            forwardInSession( sipServletRequest );
            }
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            logger.severe( "WHAT SHOULD I DO KNOW? " );
        }
    }

    protected void doEvent( SipServletRequest request ) throws ServletException, IOException {
    }

    protected void doInitialInvite( SipServletRequest sipServletRequest ) throws OpxiException, ServletException, IOException {
    }

    protected void doReInvite( SipServletRequest sipServletRequest ) throws ServletException, IOException {
    }
//
//    protected void doBye(SipServletRequest req) throws ServletException, IOException {
//    }
//
//    protected void doAck(SipServletRequest req) throws ServletException, IOException {
//    }
//
//    protected void doCancel(SipServletRequest req) throws ServletException, IOException {
//    }

    protected void doPublish( SipServletRequest request ) throws ServletException, IOException {
    }

    protected void doRefer( SipServletRequest request ) throws ServletException, IOException {
    }

    /*private DispatchContext getDispatchContext( SipSession session ) {
        DispatchContext ctx = (DispatchContext)session.getAttribute( DISPATCH_CONTEXT );
        if( ctx == null ) {
            ctx = new DispatchContext();
            logger.finer( "Setting new DP into session: " + ctx );
            session.setAttribute( DISPATCH_CONTEXT, ctx );
        }
        ctx.setActiveServlet( this.getServletName() );
        return ctx;
    }*/

    /*protected final void forward( SipServletRequest request, String servlet ) throws ServletException, IOException {
        *//*DispatchContext dc = getDispatchContext( request.getSession() );
        dc.addDispatcher( servlet );*//*
        getServletContext().getNamedDispatcher( servlet ).forward( request, null );
    }*/


    private Object getAttribute( String attr_name ) throws ServletException {
        if (getServletContext().getAttribute( attr_name ) != null)
            return getServletContext().getAttribute( attr_name );
        throw new ServletException( "No " + attr_name + " context attribute bound to OpxiSipServlet" );
    }

    /**
     * Returns Sip Servlet spec. SipFactory object for
     * opxiCallManager context.
     *
     * @return Current initialized SipFactory object in the
     *         opxiCallManager context
     * @throws ServletException
     */
    protected TimerService getTimerService() throws ServletException {
        return (TimerService) getAttribute( TIMER_SERVICE );
    }

    /**
     * Returns Sip Servlet spec. SipFactory object for
     * opxiCallManager context.
     *
     * @return Current initialized SipFactory object in the
     *         opxiCallManager context
     * @throws ServletException
     */
    protected SipFactory getSipFactory() throws ServletException {
//        try {
        return (SipFactory) getAttribute( SIP_FACTORY );
//        } catch ( ServletException e ) {
//            logger.log( Level.SEVERE, e.getMessage(), e );
//            throw new OpxiException( e.getMessage(), e );
//        }
    }

    protected ServiceFactory getServiceFactory() throws ServletException {
//        try {
        return (ServiceFactory) getAttribute( SERVICE_FACTORY );
//        } catch ( ServletException e ) {
//            throw new OpxiException( e );
//        }
    }

    /**
     * Any OpxiSipServlet who wants authentication to be enabled should
     * implement this method and return true in its cases.
     *
     * @param request incoming SIP request
     * @return true if request should be validated against
     *         OpxiCallManager AuthenticationService.
     */
    protected boolean toValidate( SipServletRequest request ) throws ServletException {
        return false;
    }

    /**
     * Validates a SIP Request against security semantics of the
     * OpxiCallManager as specified by RFC3261 section 22:
     * "Usage of HTTP Authentication".
     *
     * @param request
     * @throws NoAuthorizationHeaderAvailable If no SIP Authorization header
     *                                        exists
     *                                        Response made exists by the client.
     * @throws InvalidBindException           If clients credentials are not valid.
     */
    private void validate( SipServletRequest request ) throws OpxiException {
        if (!hasAuthorizationHeader( request ))
            throw new NoAuthorizationHeaderAvailable( "Authorization Header Not Available." );
        chkAuthentication( request );
    }

    /**
     * Creates an Unauthorized 401 challenge containing the "WWW-Authenticate"
     * header for HTTP like Digest authentication. [see RFC3261, section 22 ]
     *
     * @param request sip request
     * @return SipServletResponse object representing the challenge.
     */
    private SipServletResponse create401Challenge( SipServletRequest request ) {
        SipServletResponse response = request.createResponse( SipServletResponse.SC_UNAUTHORIZED );
        String header = SIPDigestMD5Server.createDigestChallenge( request.getCallId(), DOMAIN );
        response.setHeader( WWW_AUTHENTICATE, header );
        return response;
    }

    /**
     * Checks if input sip request has an "Authorization" header exists.
     *
     * @param request request
     * @return true if input contains any "Authorization" header.
     */
    private boolean hasAuthorizationHeader( SipServletRequest request ) {
        return request.getHeader( AUTHORIZATION ) != null;
    }

    protected void sendErrorResponse( SipServletRequest request, Throwable e ) throws ServletException, IOException {
        if (!request.isCommitted()) {
            request.createResponse( SipServletResponse.SC_SERVER_INTERNAL_ERROR, e.getMessage() ).send();
        }
    }

    /**
     * Checks if exists "Authorization" header claims the user agent
     * client, one to be.
     *
     * @param request
     * @throws com.basamadco.opxi.callmanager.sip.security.DigestResponseException
     *
     * @throws InvalidBindException
     */
    private void chkAuthentication( SipServletRequest request ) throws OpxiException {
        if (!SIPDigestMD5Server.bind( request.getMethod(), request.getHeader( AUTHORIZATION ) ))
            throw new InvalidBindException( "Invalid Credentials" );
    }

    /**
     * Copies the contents of msg1 to msg2.
     */
    protected void copyContent( SipServletMessage fromMsg, SipServletMessage toMsg ) {
        try {
            if (fromMsg.getContentType() != null) {
                toMsg.setContent( fromMsg.getRawContent(), fromMsg.getContentType() );
            }
        } catch ( IOException ex ) {
            logger.log( Level.SEVERE, ex.getMessage(), ex );
        }
    }

    /**
     * Proxies an initial SIP request to the specified list of contact locations.
     *
     * @param request incoming SIP initial request
     * @param targets a URI list
     * @throws ServletException
     */
    protected void initialProxy( SipServletRequest request, List targets ) throws ServletException, IOException {
        if (targets.size() == 0) {
            request.createResponse( SipServletResponse.SC_DOES_NOT_EXIT_ANYWHERE ).send();
        } else {
            getProxy( request ).proxyTo( targets );
        }
    }

    protected void initialProxy( SipServletRequest request, URI targetUri ) throws ServletException, IOException {
        getProxy( request ).proxyTo( targetUri );
    }

    /**
     * Generates a list of SIP/S URI objects.
     *
     * @param registrations a list of Registration Objects
     * @return list of URI objects
     */
    protected List toContactURI( Collection<Registration> registrations ) throws ServletException {
        List uris = new ArrayList();
        if (registrations != null) {
            Iterator list = registrations.iterator();
            while (list.hasNext()) {
                Object o = list.next();
                SipURI dest = (SipURI) ( (Registration) o ).getLocation().getURI();
                // forces to proxy messages to the outworld of container
                dest.setParameter( OUTBOUND_B2BUA_LEG, "true" );
                uris.add( dest );
            }
        }
        return uris;
    }

    /**
     * Initializes the proxy object with the following opxiCallManager
     * proxy config parameters:<br>
     * "opxi.proxy.record_route" : If proxy should record route itself.
     * "opxi.proxy.statefull" : If proxy is transaction aware.
     * "opxi.proxy.supervised" : If to set response method callbacks.
     *
     * @param request incoming SipServletRequest
     * @return Proxy object configured by the SipCallManager ProxyServlet
     * @throws ServletException
     */
    private Proxy getProxy( SipServletRequest request ) throws ServletException {
        Proxy p = request.getProxy();
        p.setRecordRoute( RECORD_ROUTE );
//        p.setStateful( STATEFUL );
        p.setSupervised( SUPERVISED );
        return p;
    }

}