package com.basamadco.opxi.callmanager.entity.dao.couch;

import com.basamadco.opxi.callmanager.entity.dao.webdav.LogReportDAO;
import com.basamadco.opxi.callmanager.entity.dao.couch.CouchDAO;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.logging.doc.ServiceActivityDocument;
import com.basamadco.opxi.callmanager.logging.doc.AgentActivityDocument;
import com.basamadco.opxi.activitylog.schema.OpxiActivityLog;

import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: May 10, 2010
 *         Time: 11:46:23 AM
 */
public class CouchLogReportDAO extends CouchDAO implements LogReportDAO {

    private static final Logger logger = Logger.getLogger( CouchLogReportDAO.class.getName() );


    private OpxiActivityLog logVO;


    private String userName;

    private static final String UTF8 = "UTF8";

    public CouchLogReportDAO( BaseDAOFactory daof, String userName, OpxiActivityLog logVO ) throws DAOException {
        super( daof );
        this.userName = userName;
        this.logVO = logVO;
    }

    public void writeLog() throws DAOException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            Writer log = new BufferedWriter( new OutputStreamWriter( buffer, UTF8 ) );
            logVO.marshal( log );
            log.flush();
            if ( logVO.getAgentActivity() == null ) {
                ServiceActivityDocument doc = new ServiceActivityDocument();
                doc.setBeginTime( Long.toString( logVO.getServiceActivity().getBegin().getTime() ) );
                doc.setEndTime( Long.toString( logVO.getServiceActivity().getEnd().getTime() ) );
                doc.setLog( buffer.toByteArray() );
                doc.setOwner( userName );
                doc.setId( logVO.getId() );
                getDb().createDocument( doc );

            } else {
                AgentActivityDocument doc = new AgentActivityDocument();
                doc.setBeginTime( Long.toString( logVO.getAgentActivity().getBegin().getTime() ) );
                doc.setEndTime( Long.toString( logVO.getAgentActivity().getEnd().getTime() ) );
                doc.setLog( buffer.toByteArray() );
                doc.setOwner( userName );
                doc.setAgentName( logVO.getAgentActivity().getAgent() );
                doc.setId( logVO.getId() );
                getDb().createDocument( doc );
            }
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
        }
    }

}
