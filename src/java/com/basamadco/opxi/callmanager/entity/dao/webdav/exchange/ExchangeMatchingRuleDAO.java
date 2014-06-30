package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.MatchingRuleDAO;
import com.basamadco.opxi.callmanager.pool.PoolTarget;
import com.basamadco.opxi.callmanager.pool.rules.MatchingRuleParser;
import com.basamadco.opxi.callmanager.pool.rules.Rule;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.*;

/**
 * @author Jrad
 *         Date: Oct 5, 2006
 *         Time: 2:33:28 PM
 */
public class ExchangeMatchingRuleDAO extends ExchangeDAO implements MatchingRuleDAO {

    private static final Logger logger = Logger.getLogger( ExchangeMatchingRuleDAO.class.getName() );


    private static final String RESOURCE_NAME = "matching-rule.xml";

    private static final String HIERARCHY = "callmanager";


//    private PoolTarget pool;

    public ExchangeMatchingRuleDAO( BaseDAOFactory daof, String path ) {
        super( daof, path );
    }

    protected String getResourceName() {
        return RESOURCE_NAME;
    }


    public String getHierarchy() {
        return HIERARCHY;
    }

    public Rule getMatchingRule( PoolTarget pool ) throws DAOException {
        try {
            return MatchingRuleParser.parse( getResource().toString(), pool );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }

    public void updateMatchingRule( String matchingRuleXML ) throws DAOException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Writer log = new BufferedWriter( new OutputStreamWriter( buffer , "UTF8" ) );
            log.write( matchingRuleXML );
            log.flush();
            putResource( buffer );
        } catch ( IOException e ) {
            logger.severe( e.getMessage() );
            throw new DAOException( e.getMessage(), e );
        }
    }

    /*private StringBuffer exchangeObjectToXMLFile( String matchingRule ) throws DAOException {
        try {
            File tempFile = File.createTempFile( getExchangeUsername(), getResourceName() );
            OutputStreamWriter fileWriter = new OutputStreamWriter( new FileOutputStream( tempFile ) );
            fileWriter.write( matchingRule );
            fileWriter.flush();
            fileWriter.close();
            return tempFile;
        } catch( Exception e ) {
            throw new DAOException( e.getMessage() );
        }
    }*/
    
}
