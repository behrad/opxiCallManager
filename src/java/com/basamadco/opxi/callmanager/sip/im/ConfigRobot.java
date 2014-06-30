package com.basamadco.opxi.callmanager.sip.im;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.queue.Queue;
import com.basamadco.opxi.callmanager.queue.QueueManagementService;
import com.basamadco.opxi.callmanager.sip.CallManagerAppLoader;
import com.basamadco.opxi.callmanager.sip.OpxiSipServlet;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.util.ResourceBundleUtil;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.Address;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Apr 17, 2006
 *         Time: 10:05:30 AM
 */
public class ConfigRobot extends OpxiSipServlet {

    private final static Logger logger = Logger.getLogger( ConfigRobot.class.getName() );


    public static final String[] COMMANDS = {"opxi:", "queue:", "pool:"};

    public static final String AUTHORIZE = "authorize";


    private static final String[] QMS_CMDS = {"show"};


    private static final String NO_SUCH_COMMAND_ERROR = ResourceBundleUtil.getMessage( "callmanager.IMrobot.noSuchCmd" );


    private static final String COMMAND_SUCCESSFULL = ResourceBundleUtil.getMessage( "callmanager.IMrobot.cmd.successfull" );


    protected void doMessage( SipServletRequest request ) throws ServletException, IOException {
        if ( request.getContentLength() > 0 ) {
            if ( request.getContentType().startsWith( MIME_TEXT_PLAIN ) ) {
                request.createResponse( SipServletResponse.SC_OK ).send();
                String command = request.getContent().toString();
                logger.info( "Got command: " + command );
                String result = run( request, command );
                logger.info( "Command[" + command + "] returned: " + result );

                UserAgent ua = new UserAgent( request.getFrom().getURI() );
                Address contact = request.getAddressHeader( CONTACT );
                Registration reg = new Registration( ua, contact );
                getServiceFactory().getSipService().sendIM( reg, result );
            } else {
                request.createResponse( SipServletResponse.SC_OK, "Not Implemented" ).send();
            }
        } else {
            request.createResponse( SipServletResponse.SC_OK ).send();
        }
    }

    private String run( SipServletRequest request, String command ) {
        int index = getCommandIndex( command );
        if ( index < 0 ) {
            return NO_SUCH_COMMAND_ERROR;
        }
        try {
            switch ( index ) {
                case 0:
                    return runOpxiCommand( command.split( COMMANDS[index] )[1] );
                case 1:
                    return runQMSCommand( command.split( COMMANDS[index] )[1] );
                case 2:
                    return runPoolCommand( request, command.split( COMMANDS[index] )[1] );
                default:
                    return NO_SUCH_COMMAND_ERROR;
            }
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            return e.getMessage();
        }
    }

    private String runOpxiCommand( String command ) throws OpxiException {
        Object[] params = new Object[0];
        Class[] types = new Class[0];
        try {
            CallManagerAppLoader.class.getMethod( command, types ).invoke( this, params );
            return COMMAND_SUCCESSFULL;
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new OpxiException( e );
        }
    }

    private String runQMSCommand( String command ) throws OpxiException {
        if ( command.indexOf( QMS_CMDS[0] ) > -1 ) {
            return runQMSShowCmd( command.split( " " )[1] );
        } else {
            return setQMSOption( command );
        }
    }

    private String runPoolCommand( SipServletRequest request, String command ) throws OpxiException {
        try {
            if ( command.toLowerCase().indexOf( AUTHORIZE.toLowerCase() ) > 0 ) {
                String poolName = command.split( "\\." )[0];
                String ruleId = command.split( "\\." )[1];
                String agent = command.split( "\\(" )[1].split( "\\)" )[0];
                UserAgent ua = new UserAgent( agent, OpxiToolBox.getLocalDomain() );
                String manager = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getManagerNameFor( agent );
                if ( ((SipURI) request.getFrom().getURI()).getUser().equalsIgnoreCase( manager ) ) {
                    getServiceFactory().getPoolService().authorizeNextUsage( poolName, ruleId, ua );
                    return COMMAND_SUCCESSFULL;
                }
                return "Not Authorized. This command should be issued by '" + agent + "'s manager: '" + manager + "'";
            } else {
                return "Command not supported for PoolService: '" + command + "'";
            }
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            return e.getMessage();
        }
    }

    private String runQMSShowCmd( String queueName ) throws OpxiException {
        StringBuffer buffer = new StringBuffer();
        Object[] msgs = new String[]{"Does not supported yet :("};
        for ( int i = 0; i < msgs.length; i++ ) {
            buffer.append( msgs[i] ).append( "\r\n" );
        }
        return buffer.toString();
    }

    private String setQMSOption( String command ) throws OpxiException {
        String param = command.split( "\\(" )[0];
        String name = command.split( "\\(" )[1].split( "\\)" )[0];
        String value = command.split( "=" )[1];
        try {
            QueueManagementService qms = getServiceFactory().getQueueManagementService();
            Queue queue = qms.queueForName( name );
            Object entity = queue;
            entity.getClass().getMethod( param, new Class[]{String.class} ).invoke( entity, new Object[]{value} );
            return COMMAND_SUCCESSFULL;
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw e;
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new OpxiException( e );
        }
    }

    private int getCommandIndex( String command ) {
        for ( int i = 0; i < COMMANDS.length; i++ ) {
            if ( command.indexOf( COMMANDS[i] ) > -1 ) {
                return i;
            }
        }
        return -1;
    }

    public void destroy() {
    }

}