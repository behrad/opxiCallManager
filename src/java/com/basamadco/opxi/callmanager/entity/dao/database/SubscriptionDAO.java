package com.basamadco.opxi.callmanager.entity.dao.database;

import com.basamadco.opxi.callmanager.entity.Subscription;
import com.basamadco.opxi.callmanager.entity.SubscriptionNotAvailableException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAO;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

import java.util.List;

public interface SubscriptionDAO extends DatabaseDAO {
	
	public List find( UserAgent ua ) throws DAOException;
	
	public Subscription find( String sessionId, UserAgent subscriber, UserAgent notifier ) throws DAOException, SubscriptionNotAvailableException;
	
}
