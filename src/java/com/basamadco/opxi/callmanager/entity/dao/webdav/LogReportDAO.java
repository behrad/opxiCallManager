package com.basamadco.opxi.callmanager.entity.dao.webdav;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAO;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

/**
 * @author Jrad
 *         Date: Oct 25, 2006
 *         Time: 1:50:59 PM
 */
public interface LogReportDAO extends BaseDAO {

    public void writeLog() throws DAOException;

}
