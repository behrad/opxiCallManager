package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

import com.novell.ldap.LDAPSocketFactory;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.connectionpool.PoolManager;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryConnectionManager;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.OpxiException;

import java.net.Socket;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Added for the needs of an Ldap connection pool manager
 *
 * @author Jrad
 *         Date: Mar 8, 2007
 *         Time: 1:07:57 PM
 */
public class LdapConnectionManager implements DirectoryConnectionManager {

    private static final Logger logger = Logger.getLogger( LdapConnectionManager.class.getName() );

    /**
     * DIRECTORY server version which is used by the client
     */
    private static final int LDAP_VERSION = LDAPConnection.LDAP_V3;

    /**
     * DIRECTORY server host name
     */
    public static final String LDAP_HOST = PropertyUtil.getProperty( "opxi.ldap.host.name" );

    /**
     * DIRECTORY server port to connect to
     */
    private static final int LDAP_PORT = Integer.parseInt( PropertyUtil.getProperty( "opxi.ldap.host.port" ) );


    private static final int LDAP_MAX_CONNECTIONS =
            Integer.parseInt( PropertyUtil.getProperty( "opxi.ldap.pool.maxConnections" ) );


    private static final int LDAP_MAX_SHARED_CONNECTIONS =
            Integer.parseInt( PropertyUtil.getProperty( "opxi.ldap.pool.maxSharedConnections" ) );


    private static final int LDAP_CONNECTION_TIMEOUT =
            Integer.parseInt( PropertyUtil.getProperty( "opxi.ldap.connectionTimeout" ) );


    private PoolManager connectionPool;


    public LdapConnectionManager() throws OpxiException {
        try {
            logger.fine( "Initializing LDAP Connection Pool Manager to server '" + LDAP_HOST + "'" );
            connectionPool = new PoolManager(
                    LDAP_HOST, LDAP_PORT, LDAP_MAX_CONNECTIONS, LDAP_MAX_SHARED_CONNECTIONS
                    , null
            ); // new ConnectTimeOutSocketFactory( LDAP_CONNECTION_TIMEOUT )
            // logger.finest( "after newing com.novell.ldap.connectionpool.PoolManager" );

        } catch ( LDAPException e ) {
            throw new OpxiException( e );
        }
    }

    public LDAPConnection getConnection( String username, String password ) throws DAOException {
        try {
//            logger.finest( "Get LDAP Connection for '"+username+"':'"+password+"'" );
            return connectionPool.getBoundConnection( username, password.getBytes() );
        } catch ( LDAPException e ) {
            logger.log( Level.SEVERE, "Ldap error message: " + e.getMessage() + ", " + e.getResultCode() );
            throw new DAOException( e.getMessage(), e );
        } catch ( InterruptedException e ) {
            logger.log( Level.SEVERE, "Ldap error message: " + e );
            throw new DAOException( e.getMessage(), e );
        }
    }

    public void free( LDAPConnection connection ) {
        connectionPool.makeConnectionAvailable( connection );
    }

    /*public void destroy() {
        connectionPool.
    }*/
}

/**
 * This factory will return connected sockets with the added capability to
 * timeout if the sockets do not connect within a specified time limit. This
 * uses an additional thread to Connect.
 * <p/>
 * The socket may not connect for a large amount of time. Even
 * though the program will be able to continue processing and possibily
 * connect to another server, this additional thread will remain alive.
 * This is because the socket will remained blocked and the thread will
 * remain in memory until the socket quits.
 * <p/>
 * WARNING: Since every connection timeout may leave a thread lingering in
 * memory, a large amount of connection timeouts may eventually fill up
 * available space in memory.
 */
class ConnectTimeOutSocketFactory implements LDAPSocketFactory, java.lang.Runnable {

    protected long timeout;

    protected LDAPSocketFactory factory;

    protected String host;

    protected int port;

    protected Socket socket = null;

    protected IOException socketError = null;

    protected boolean hasTimedOut = false;

    /**
     * Constructor sets the timeout value for
     */
    public ConnectTimeOutSocketFactory( long timeout, LDAPSocketFactory factory ) {
        this.timeout = timeout;
        this.factory = factory;
    }

    public ConnectTimeOutSocketFactory( long timeout ) {
        this.timeout = timeout;
        this.factory = null;
    }

    public java.net.Socket createSocket( String host, int port )
            throws java.io.IOException {

        this.host = host;
        this.port = port;
        this.socket = null;
        this.socketError = null;

        Thread r = new Thread( this );
        r.setDaemon( true ); // If this is the last thread running, allow exit.
        r.start();

        try {
            r.join( timeout );
        } catch ( java.lang.InterruptedException ie ) {
            r.interrupt();
        }

        /* if an error occured creating the socket then throw the error */
        if ( socketError != null ) {
            throw socketError;
        }

        /* if the socket is null then the socket connect has not completed*/
        if ( socket == null ) {
            hasTimedOut = true;
            throw new IOException( "Socket connection timed out: " +
                    host + ":" + port );
            /* at this point we leave the connect
      thread to take care of itself */
        } else {
            System.out.println( "Connected to the server socket" );
        }
        return socket;
    }

    /**
     * This thread will create and connect the socket.  The connection will
     * block until it is complete.  If an exception occurs it is saved for
     * the main program thread to pick up and throw.
     */
    public void run() {
        try {
            if ( factory == null ) {
                //this constructor will block until a connection is made
                socket = new Socket( host, port );
            } else {
                //this method will likely block until a connection is made
                socket = factory.createSocket( host, port );
            }
        } catch ( IOException ioe ) {
            this.socketError = ioe;
        }

        if ( hasTimedOut ) {
            try {
                socket.close();
            } catch ( IOException ioe ) {
                // we don't care about this exeption
            }
            ;
            socket = null;
            socketError = null;
        }
        return;
    }
}
