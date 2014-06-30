package com.basamadco.opxi.callmanager.entity.dao.database;

import com.basamadco.opxi.callmanager.entity.Presence;
import com.basamadco.opxi.callmanager.entity.PresenceNotAvailableException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

public interface PresenceDAO extends DatabaseDAO {
	
	public Presence find( String location ) throws PresenceNotAvailableException, DAOException;

    public Presence findbyAgent( UserAgent ua ) throws PresenceNotAvailableException, DAOException;
}
