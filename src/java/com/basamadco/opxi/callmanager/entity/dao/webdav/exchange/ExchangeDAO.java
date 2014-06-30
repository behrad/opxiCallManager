package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.WebdavDAO;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavResource;

import java.io.*;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 10:56:11 AM
 */
public abstract class ExchangeDAO implements WebdavDAO {

    private static final Logger logger = Logger.getLogger( ExchangeDAO.class.getName() );


    public static final String CALL_MANAGER_WEBDAV_HOME = PropertyUtil.getProperty( "opxi.callmanager.exchange.homeURI" );


    protected static final String EXCHANGE_USERNAME = PropertyUtil.getProperty( "opxi.callmanager.exchange.username" );

    protected static final String EXCHANGE_PASSWORD = PropertyUtil.getProperty( "opxi.callmanager.exchange.password" );

    //    protected static final String EXCHANGE_DOMAIN = PropertyUtil.getProperty( "opxi.logReport.exchange.domain" );
    protected static final String EXCHANGE_DOMAIN = OpxiToolBox.getLocalDomain();

    protected static final char DOMAIN_SEPERATOR = '\\';

    protected static final char PATH_SEPERATOR = '/';


    protected BaseDAOFactory daof;

    protected String exchangeUsername;

    protected String exchangePass;

    protected String path;


    public ExchangeDAO( BaseDAOFactory daof, String userName, String path ) throws DAOException {
        this.daof = daof;
        this.exchangeUsername = userName;
        this.path = path;
        try {
            String passwd = BaseDAOFactory.getDirectoryDAOFactory().getAgentDAO().getUserPassword( userName );
            this.exchangePass = passwd;
        } catch ( DAOFactoryException e ) {
            throw new DAOException( e.getMessage() );
        }
    }

    public ExchangeDAO( BaseDAOFactory daof, String path ) {
        this.daof = daof;
        this.path = path;
        this.exchangeUsername = EXCHANGE_USERNAME;
        this.exchangePass = EXCHANGE_PASSWORD;
    }

    public BaseDAOFactory getDAOFactory() {
        return daof;
    }

    protected String getResourcePath( String name ) {
        if ( name == null ) {
            name = getResourceName();
        }
        return getPath() + PATH_SEPERATOR + getHierarchy() + PATH_SEPERATOR + name;
    }

    protected abstract String getResourceName();


    public String getExchangePass() {
        return exchangePass;
    }

    public String getExchangeUsername() {
        return exchangeUsername;
    }

    public String getPath() {
        return path;
    }


    public StringBuffer getResource() throws DAOException {
        return getResource( getResourceName() );
    }

    public StringBuffer getResource( String name ) throws DAOException {
        BufferedReader reader = null;
        WebdavResource resource = null;
        StringBuffer buffer = null;
        try {
            HttpURL url = new HttpURL( getPath() );
            logger.finest( "Try to connect to webdav resource '"+getPath()+"' with userinfo: " + EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername() );
            url.setUserinfo( EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername(), getExchangePass() );
            resource = new WebdavResource( url, WebdavResource.BASIC, 0 );
            resource.setDebug( 0 );
            if ( resource.getStatusCode() == 200 ) {
                reader = new BufferedReader( new InputStreamReader(
                        resource.getMethodData( getResourcePath( name ) ) ) );
                buffer = new StringBuffer();
                String s = reader.readLine();
                while ( s != null ) {
                    buffer.append( s );
                    s = reader.readLine();
                }
            } else {
                throw new DAOException( resource.getStatusMessage() );
            }
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( "Check if resource '" + getResourcePath( name ) + "' already exists!" );
        } finally {
            /*try {
                if( resource != null ) {
                    resource.unlockMethod();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }*/
        }
        return buffer;
    }


    public void putResource( ByteArrayOutputStream buffer ) throws DAOException {
        putResource( getResourceName(), buffer, null );
    }

    public void putResource( ByteArrayOutputStream buffer, Hashtable properties ) throws DAOException {
        putResource( getResourceName(), buffer, properties );
    }

    public void putResource( String name, ByteArrayOutputStream buffer ) throws DAOException {
        putResource( name, buffer, null );
    }

    public void putResource( String name, ByteArrayOutputStream buffer, Hashtable props ) throws DAOException {
        WebdavResource resource = null;
        try {
            HttpURL url = new HttpURL( getPath() );
            logger.finest( "Try to connect to webdav resource '"+getPath()+"' with userinfo: " + EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername() );
            url.setUserinfo( EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername(), getExchangePass() );
            logger.finest( "*************************** before new WebdavResource()" );
            resource = new WebdavResource( url, WebdavResource.BASIC, 0 );
            resource.setDebug( 0 );
            logger.finest( "*************************** after new WebdavResource()" );
            if ( resource.getStatusCode() >= 200 && resource.getStatusCode() < 300 ) {
                boolean succeeded = false;
                logger.finest( "*************************** before putMethod()" );
                succeeded = resource.putMethod( getResourcePath( name ), buffer.toByteArray() );
                logger.finest( "*************************** after putMethod()" );
                if ( !succeeded ) {
                    logger.severe( resource.getStatusCode() + ": " + resource.getStatusMessage() );
                    throw new DAOException( "Unable to execute putMethod: " + getResourcePath( name ) + " for " + getExchangeUsername() );
                }
                if ( props != null ) {
                    logger.finest( "*************************** before propPatchMethod()" );
                    succeeded = resource.proppatchMethod( getResourcePath( name ), props, true );
                    logger.finest( "*************************** after propPatchMethod()" );
                    if ( !succeeded ) {
                        logger.severe( resource.getStatusCode() + ": " + resource.getStatusMessage() );
                        throw new DAOException( "Unable to execute proppatchMethod: " + getResourcePath( name ) + " for " + getExchangeUsername() );
                    }
                }
            } else {
                logger.log( Level.SEVERE, resource.getPath() + ": " + resource.getStatusMessage() );
                throw new DAOException( resource.getStatusMessage() );
            }
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage() + ": " + getPath(), e );
            throw new DAOException( "Check if resource '" + getPath() + "' already exists!" );
        } finally {
            /*try {
                if( resource != null ) {
                    logger.finest( "*************************** before unlock()" );
                    resource.unlockMethod();
                    logger.finest( "*************************** after unlock()" );
                }
            } catch ( IOException e ) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }*/
        }
    }

    public void putResource( String name, byte[] content ) throws DAOException {
        putResource( name, content, null );
    }

    public void putResource( String name, byte[] content, Hashtable props ) throws DAOException {
        WebdavResource resource = null;
        try {
            HttpURL url = new HttpURL( getPath() );
            logger.finest( "Try to connect to webdav resource '"+getPath()+"' with userinfo: " + EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername() );
            url.setUserinfo( EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername(), getExchangePass() );
            resource = new WebdavResource( url, WebdavResource.BASIC, 0 );
            resource.setDebug( 0 );
            if ( resource.getStatusCode() >= 200 && resource.getStatusCode() < 300 ) {
                boolean succeeded = false;
                succeeded = resource.putMethod( getResourcePath( name ), content );
                if ( !succeeded ) {
                    logger.severe( resource.getStatusCode() + ": " + resource.getStatusMessage() );
                    throw new DAOException( "Unable to execute putMethod: " + getResourcePath( name ) + " for " + getExchangeUsername() );
                }
                if ( props != null ) {
                    succeeded = resource.proppatchMethod( getResourcePath( name ), props, true );
                    if ( !succeeded ) {
                        logger.severe( resource.getStatusCode() + ": " + resource.getStatusMessage() );
                        throw new DAOException( "Unable to execute proppatchMethod: " + getResourcePath( name ) + " for " + getExchangeUsername() );
                    }
                }
            } else {
                throw new DAOException( resource.getStatusMessage() );
            }
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage() + ": " + getResourcePath( name ), e );
            throw new DAOException( "Check if resource '" + getPath() + "' already exists!" );
        } finally {
            /*try {
                if( resource != null ) {
                    resource.unlockMethod();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }*/
        }
    }

    public void deleteResource( String name ) throws DAOException {
        WebdavResource resource = null;
        try {
            HttpURL url = new HttpURL( getPath() );
            url.setUserinfo( EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername(), getExchangePass() );
            resource = new WebdavResource( url, WebdavResource.BASIC, 0 );
            resource.setDebug( 0 );
            if ( resource.getStatusCode() == 200 ) {
                boolean succeeded = false;
                succeeded = resource.deleteMethod( getResourcePath( name ) );
                if ( !succeeded ) {
                    logger.severe( resource.getStatusCode() + ": " + resource.getStatusMessage() );
                    throw new DAOException( "Unable to execute putMethod: " + getResourcePath( name ) + " for " + getExchangeUsername() );
                }
            } else {
                throw new DAOException( resource.getStatusMessage() );
            }
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage() + ": " + getResourcePath( name ), e );
            throw new DAOException( "Check if resource '" + getPath() + "' already exists!" );
        } finally {
            /*try {
                if( resource != null ) {
                    resource.unlockMethod();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }*/
        }
    }

    public void createHierarchy() throws DAOException {
        String[] dirs = getHierarchy().split( "/" );
        String pathToCreate = getPath();
        for ( int i = 0; i < dirs.length; i++ ) {
            String subDir = dirs[i];
            pathToCreate += "/" + subDir;
            try {
                mkdir( pathToCreate );
            } catch ( DAOException e ) {
                logger.warning( e.getMessage() );
//                throw e;
            }
        }
    }

    public void deleteHierarchy() throws DAOException {
        String[] dirs = getHierarchy().split( "/" );
        String pathToDelete = getPath();
        for ( int i = 0; i < dirs.length; i++ ) {
            String subDir = dirs[i];
            pathToDelete += "/" + subDir;
            try {
                rmdir( pathToDelete );
            } catch ( DAOException e ) {
                logger.warning( e.getMessage() );
//                throw e;
            }
        }
    }

    public void mkdir( String dir ) throws DAOException {
        WebdavResource resource = null;
        try {
            HttpURL url = new HttpURL( getPath() );
            logger.finest( "Try to connect to webdav resource '"+getPath()+"' with userinfo: " + EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername() );
            url.setUserinfo( EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername(), getExchangePass() );
            resource = new WebdavResource( url, WebdavResource.BASIC, 0 );
            resource.setDebug( 0 );
            if ( resource.getStatusCode() >= 200 && resource.getStatusCode() < 300 ) {
                boolean succeeded = false;
                succeeded = resource.mkcolMethod( dir );
//                succeeded = resource.deleteMethod( "http://opxiappserver/exchange/agent01/opxi/test" );
                if ( !succeeded ) {
                    logger.severe( resource.getStatusCode() + ": " + resource.getStatusMessage() );
                    if ( resource.getStatusCode() == 405 ) {
                        throw new DAOException( "Directory Already Exists: '" + dir + "'", 405 );
                    }
                    throw new DAOException( "Unable to execute mkcolMethod for " + dir );
                }
//                Hashtable props = new Hashtable();
//                props.put( new PropertyName( Constants.DAV, "contentclass" ), "urn:content-classes:reportmessage" );
//                succeeded = resource.proppatchMethod( getProfilePath(), props, true );
//                if( !succeeded ) {
//                    throw new DAOException( "Unable to execute proppatchMethod: " + getProfilePath() + " for " + getExchangeUsername() );
//                }
            } else {
                throw new DAOException( resource.getStatusMessage() );
            }
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage() + ": " + getPath(), e );
            throw new DAOException( e.getMessage(), e );
        } finally {
            /*try {
                if( resource != null ) {
                    resource.unlockMethod();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }*/
        }
    }

    public void rmdir( String dir ) throws DAOException {
        WebdavResource resource = null;
        try {
            HttpURL url = new HttpURL( getPath() );
            url.setUserinfo( EXCHANGE_DOMAIN + DOMAIN_SEPERATOR + getExchangeUsername(), getExchangePass() );
            resource = new WebdavResource( url, WebdavResource.BASIC, 0 );
            resource.setDebug( 0 );
            if ( resource.getStatusCode() == 200 ) {
                boolean succeeded = false;
                succeeded = resource.deleteMethod( dir );
                if ( !succeeded ) {
                    logger.severe( resource.getStatusCode() + ": " + resource.getStatusMessage() );
                    if ( resource.getStatusCode() == 405 ) {
                        throw new DAOException( "Directory Already Exists: '" + dir + "'", 405 );
                    }
                    throw new DAOException( "Unable to execute deleteMethod for " + dir );
                }
//                Hashtable props = new Hashtable();
//                props.put( new PropertyName( Constants.DAV, "contentclass" ), "urn:content-classes:reportmessage" );
//                succeeded = resource.proppatchMethod( getProfilePath(), props, true );
//                if( !succeeded ) {
//                    throw new DAOException( "Unable to execute proppatchMethod: " + getProfilePath() + " for " + getExchangeUsername() );
//                }
            } else {
                throw new DAOException( resource.getStatusMessage() );
            }
        } catch ( IOException e ) {
            logger.log( Level.SEVERE, e.getMessage() + ": " + getPath(), e );
        } catch ( DAOException e ) {
            e.printStackTrace();
//            logger.error( e );
        } finally {
            /*try {
                if( resource != null ) {
                    resource.unlockMethod();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }*/
        }
    }

}