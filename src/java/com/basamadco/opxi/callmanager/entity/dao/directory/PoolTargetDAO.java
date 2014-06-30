package com.basamadco.opxi.callmanager.entity.dao.directory;

import com.basamadco.opxi.callmanager.entity.dao.DAOException;


/**
 * @author Jrad
 */
public interface PoolTargetDAO extends DirectoryDAO {

    public String[] getMembers( String groupCN ) throws DAOException;

    public boolean isGroup( String CN ) throws DAOException;    

//    public float getPSSC( String CN ) throws DAOException;
//
//    public float getSSSC( String CN ) throws DAOException;

    public String getHomeURI( String poolName ) throws DAOException;
}
