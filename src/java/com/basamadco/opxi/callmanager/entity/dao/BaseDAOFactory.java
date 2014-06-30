package com.basamadco.opxi.callmanager.entity.dao;

import com.basamadco.opxi.callmanager.entity.dao.database.hbm.HibernateDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.ldap.LdapDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.StorageDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.Storage;
import com.basamadco.opxi.callmanager.entity.dao.webdav.exchange.ExchangeDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.couch.CouchDAOFactory;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Feb 5, 2006
 *         Time: 10:58:24 AM
 */
public abstract class BaseDAOFactory {

//    public static final int DATABASE  = 1;

    public static final int DIRECTORY = 2;

    public static final int WEBDAV = 3;

    public static final int COUCH = 4;

    protected static final Logger logger = Logger.getLogger( BaseDAOFactory.class.getName() );

    private static DirectoryDAOFactory directoryDAOF;

    private static DatabaseDAOFactory databaseDAOF;

    private static StorageDAOFactory webdavDAOF;

    private static CouchDAOFactory couchDAOF;


    public static BaseDAOFactory getDAOFactory( int whichFactory ) throws DAOFactoryException {
        switch ( whichFactory ) {
//            case DATABASE :
//                return createHibernateDAOFactorySingleton();
            case DIRECTORY:
                return createLdapDAOFactorySingleton();
            case WEBDAV:
                return createExchangeDAOFactorySingleton();
            case COUCH:
                return createCouchDAOFactorySingleton();
            default:
                throw new DAOFactoryException( "Not such a DAOFactory exists: " + whichFactory );
        }
    }


    public static CouchDAOFactory getCouchDAOFactory() throws DAOFactoryException {
        return (CouchDAOFactory) getDAOFactory( COUCH );
    }

    public static DirectoryDAOFactory getDirectoryDAOFactory() throws DAOFactoryException {
        return (DirectoryDAOFactory) getDAOFactory( DIRECTORY );
    }

    public static ExchangeDAOFactory getWebdavDAOFactory() throws DAOFactoryException {
        return (ExchangeDAOFactory) getDAOFactory( WEBDAV );
    }

    public static StorageDAOFactory getConfigStorageDAOFactory() throws DAOFactoryException {
        if ( StorageDAOFactory.STORAGE_TYPE == Storage.exchange ) {
            return getWebdavDAOFactory();
        } else if ( StorageDAOFactory.STORAGE_TYPE == Storage.couchdb ) {
            return getCouchDAOFactory();
        }
        throw new DAOFactoryException( "Not supported storage DAO factory: " + StorageDAOFactory.STORAGE_TYPE );
    }

    private static DatabaseDAOFactory createHibernateDAOFactorySingleton() {
        if ( databaseDAOF == null ) {
            databaseDAOF = new HibernateDAOFactory();
        }
        return databaseDAOF;

    }

    private static DirectoryDAOFactory createLdapDAOFactorySingleton() throws DAOFactoryException {
        if ( directoryDAOF == null ) {
            directoryDAOF = new LdapDAOFactory();
        }
        // logger.finest( "Returning directoryDAOF : " + directoryDAOF);
        return directoryDAOF;
    }

    private static StorageDAOFactory createExchangeDAOFactorySingleton() {
        if ( webdavDAOF == null ) {
            webdavDAOF = new ExchangeDAOFactory();
        }
        return webdavDAOF;
    }

    private static CouchDAOFactory createCouchDAOFactorySingleton() throws DAOFactoryException {
        if ( couchDAOF == null ) {
            couchDAOF = new com.basamadco.opxi.callmanager.entity.dao.couch.CouchDAOFactory();
        }
        return couchDAOF;
    }

}