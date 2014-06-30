package com.basamadco.opxi.callmanager.entity.dao.directory;

import com.basamadco.opxi.callmanager.entity.dao.DAOException;

import java.util.List;


public interface AgentDAO extends DirectoryDAO {

    public boolean isMemberOf( String agentCN, String groupCN ) throws DAOException;

    public String getUserPassword( String username ) throws DAOException;

    public String getUsernameFor( String phoneNumber ) throws DAOException;

    public String getManagerNameFor( String agentCN ) throws DAOException;

    public List getPoolMemberships( String agentCN ) throws DAOException;

    public String getHomeURI( String agentCN ) throws DAOException;

}
