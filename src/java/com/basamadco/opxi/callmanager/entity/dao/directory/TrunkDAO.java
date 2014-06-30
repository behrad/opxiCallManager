package com.basamadco.opxi.callmanager.entity.dao.directory;

import com.basamadco.opxi.callmanager.entity.dao.DAOException;

/**
 * User: AM
 * Date: Sep 12, 2007
 * Time: 4:04:52 PM
 */
public interface TrunkDAO extends DirectoryDAO {

    /*
    public String getName(String strName) throws DAOException;

    public String getDescription(String strDesc) throws DAOException;

    */

    public String getStaticRoute(String strSR) throws DAOException;

    public String getDialPattern(String strPattern) throws DAOException;
    
}
