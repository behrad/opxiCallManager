package com.basamadco.opxi.callmanager.entity.dao.couch;

import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAO;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

import java.util.logging.Logger;

import org.jcouchdb.db.ServerImpl;
import org.jcouchdb.db.Database;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

/**
 * @author Jrad
 *         Date: May 7, 2010
 *         Time: 1:36:03 PM
 */
public class CouchDAO implements BaseDAO {

    private static final Logger logger = Logger.getLogger( CouchDAO.class.getName() );


    protected static final String HTTP = "http://";

    protected static final String URL_SEPERATOR = "/";


    protected static final String COUCH_ADDRESS = PropertyUtil.getProperty( "opxi.callmanager.couchDB.server" );

    protected static final int COUCH_PORT = Integer.valueOf(
            PropertyUtil.getProperty( "opxi.callmanager.couchDB.port" ).trim() );

    protected static final String COUCH_USERNAME = PropertyUtil.getProperty( "opxi.callmanager.couchDB.username" );

    protected static final String COUCH_PASSWORD = PropertyUtil.getProperty( "opxi.callmanager.couchDB.password" );

//    protected static final String COUCH_REALM = PropertyUtil.getProperty( "opxi.callmanager.couchDB.realm" );

    protected static final String COUCH_DB_NAME = OpxiToolBox.getLocalDomain().replaceAll( "\\.", "_" );

    private BaseDAOFactory daof;

    private ServerImpl server;

    private Database db;


    public CouchDAO( BaseDAOFactory daof ) throws DAOException {
        this.daof = daof;
        server = new ServerImpl( COUCH_ADDRESS, COUCH_PORT );
        server.setCredentials(
                new AuthScope( COUCH_ADDRESS, COUCH_PORT ),
                new UsernamePasswordCredentials( COUCH_USERNAME, COUCH_PASSWORD )
        );

        db = new Database( server, COUCH_DB_NAME );
    }

    public ServerImpl getServer() {
        return server;
    }

    public Database getDb() {
        return db;
    }

    public BaseDAOFactory getDAOFactory() {
        return daof;
    }

    protected String getURL( String docId, String attachName ) {
        StringBuffer str = new StringBuffer( HTTP );
        str.append( COUCH_ADDRESS ).append( ":" ).append( COUCH_PORT ).append( URL_SEPERATOR )
                .append( getDb().getName() ).append( URL_SEPERATOR )
                .append( docId ).append( URL_SEPERATOR )
                .append( attachName );
        return str.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        getServer().shutDown();
    }
}
