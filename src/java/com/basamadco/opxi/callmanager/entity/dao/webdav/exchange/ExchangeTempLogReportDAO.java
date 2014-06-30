package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.activitylog.schema.OpxiActivityLog;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * @author Jrad
 *         Date: Oct 28, 2006
 *         Time: 10:49:49 AM
 */
public class ExchangeTempLogReportDAO extends ExchangeLogReportDAO {

    private static final Logger logger = Logger.getLogger( ExchangeTempLogReportDAO.class.getName() );


    public ExchangeTempLogReportDAO( BaseDAOFactory daof, String path, OpxiActivityLog logVO ) throws DAOException {
        super( daof, path, logVO );
        try {
            createHierarchy();
        } catch ( DAOException e ) {
            logger.log( Level.WARNING, e.getMessage(), e );
        }
    }

    public String getHierarchy() {
        return "callmanager/temporaryReports";
    }

}