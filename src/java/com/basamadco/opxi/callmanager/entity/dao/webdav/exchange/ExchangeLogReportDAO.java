package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.activitylog.schema.OpxiActivityLog;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.LogReportDAO;
import com.basamadco.opxi.callmanager.sip.util.SipUtil;
import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.callmanager.logging.doc.ServiceActivityDocument;
import com.basamadco.opxi.callmanager.logging.doc.AgentActivityDocument;
import org.apache.webdav.lib.Constants;
import org.apache.webdav.lib.PropertyName;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Oct 25, 2006
 *         Time: 1:55:56 PM
 */
public class ExchangeLogReportDAO extends ExchangeDAO implements LogReportDAO {

    private static final Logger logger = Logger.getLogger( ExchangeLogReportDAO.class.getName() );


    private static final String HIERARCHY = PropertyUtil.getProperty( "opxi.logReport.exchange.dir" );

    private static final String FROMNAME = PropertyUtil.getProperty( "opxi.logReport.exchange.fromName" );

    private static final String IMPORTANCE = PropertyUtil.getProperty( "opxi.logReport.exchange.importance" );

    private static final String AGENT_LOG_SUBJECT = PropertyUtil.getProperty( "opxi.logReport.agentLog.subject" );

    private static final String SERVICE_LOG_SUBJECT = PropertyUtil.getProperty( "opxi.logReport.serviceLog.subject" );

    private static final String UTF8 = "UTF8";


    private OpxiActivityLog logVO;

    private Hashtable logReportProperties = new Hashtable();


    protected ExchangeLogReportDAO( BaseDAOFactory daof, String path, OpxiActivityLog logVO ) throws DAOException {
        super( daof, path );
        initExchangeLogReportDAO( logVO );
    }

    public ExchangeLogReportDAO( BaseDAOFactory daof, String userName, String path, OpxiActivityLog logVO ) throws DAOException {
        super( daof, userName, path );
        initExchangeLogReportDAO( logVO );
    }

    private void initExchangeLogReportDAO( OpxiActivityLog logVO ) {
        this.logVO = logVO;
        String logType = AGENT_LOG_SUBJECT;
        if ( logVO.getAgentActivity() == null ) {
            logType = SERVICE_LOG_SUBJECT;
        } else {
            /*logReportProperties.put( new PropertyName( "urn:schemas:httpmail:", "cc" ),
                    SipUtil.getQualifiedName( logVO.getAgentActivity().getAgent() )
            );*/
//            logger.finest( "++++++++++++++++= set cc=" + SipUtil.getQualifiedName( logVO.getAgentActivity().getAgent() ) );
            logReportProperties.put( new PropertyName( "urn:schemas:httpmail:", "textdescription" ),
                    SipUtil.getQualifiedName( logVO.getAgentActivity().getAgent() )
            );
        }
        logReportProperties.put( new PropertyName( Constants.DAV, "contentclass" ), "urn:content-classes:reportmessage" );
//        logReportProperties.put( new PropertyName( "urn:schemas:httpmail:", "date" ), new Date().toString() );
//        logReportProperties.put( new PropertyName( "urn:schemas:httpmail:", "displayto" ), exchangeUsername );
        logReportProperties.put( new PropertyName( "urn:schemas:httpmail:", "subject" ), logType );
        logReportProperties.put( new PropertyName( "urn:schemas:httpmail:", "fromname" ), FROMNAME );
        logReportProperties.put( new PropertyName( "urn:schemas:httpmail:", "importance" ), IMPORTANCE );
    }

    protected String getResourceName() {
        return logVO.getId() + ".xml";
    }

    public String getHierarchy() {
        return HIERARCHY;
    }

    public void writeLog() throws DAOException {
        try {
            createHierarchy();
        } catch ( DAOException e ) {
//            logger.log( Level.SEVERE, e.getMessage(), e );
        }
        try {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Writer log = new BufferedWriter( new OutputStreamWriter( buffer, UTF8 ) );
            logVO.marshal( log );
            log.flush();
            putResource( buffer, logReportProperties );

        } catch ( MarshalException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        } catch ( ValidationException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new DAOException( e.getMessage() );
        }
    }

}