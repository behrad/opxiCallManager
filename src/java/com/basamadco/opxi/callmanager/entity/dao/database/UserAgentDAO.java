package com.basamadco.opxi.callmanager.entity.dao.database;

import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.UserNotAvailableException;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAO;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

public interface UserAgentDAO extends DatabaseDAO {
	
	public UserAgent find( String name, String domain ) throws DAOException, UserNotAvailableException;
	
	public UserAgent findOrSave( String name, String domain ) throws DAOException;

}
