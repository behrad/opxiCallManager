package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.ProfileDAO;
import com.basamadco.opxi.callmanager.entity.profile.OpxiCMEntityProfile;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 11:42:01 AM
 */
public class ExchangeProfileDAO extends ExchangeDAO implements ProfileDAO {

    private static final Logger logger = Logger.getLogger( ExchangeProfileDAO.class.getName() );

    private static final String profileName = "profile.xml";

    private static final String HIERARCHY = PropertyUtil.getProperty( "opxi.callmanager.profile.exchange.dir" );


    public ExchangeProfileDAO( BaseDAOFactory daof, String userName, String path ) throws DAOException {
        super( daof, userName, path );
    }

    public ExchangeProfileDAO( BaseDAOFactory daof, String path ) {
        super( daof, path );
    }


    public String getHierarchy() {
        return HIERARCHY;
    }

    protected String getResourceName() {
        return profileName;
    }

    public void writeProfile( final OpxiCMEntityProfile profile ) throws DAOException {
        putResource( OpxiToolBox.exchangeObjectToXMLFile( profile ) );
    }

    public OpxiCMEntityProfile readProfile() throws DAOException {
        try {
            return OpxiCMEntityProfile.unmarshal( new StringReader( getResource().toString() ) );
        } catch ( MarshalException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        } catch ( ValidationException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }


    public void updateProfile( OpxiCMEntityProfile profile ) throws DAOException {
        throw new DAOException( ExchangeProfileDAO.class + " dosent support update action!" );
    }

    public String writeResource( String name, String contentType, byte[] content ) throws DAOException {
        putResource( name, content );
        return getResourcePath( name );
    }

    public void deleteResource( String resourceName ) throws DAOException {
        super.deleteResource( resourceName );
    }

    public void deleteProfile() throws DAOException {
        rmdir( getResourcePath( profileName ) );
    }
}