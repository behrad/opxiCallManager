package com.basamadco.opxi.callmanager.entity.dao.database;

import com.basamadco.opxi.callmanager.entity.dao.*;


public abstract class DatabaseDAOFactory extends BaseDAOFactory {

	public abstract DomainDAO       getDomainDAO()       throws DAOException;
	public abstract UserAgentDAO    getUserAgentDAO()    throws DAOException;
	public abstract RegistrationDAO getRegistrationDAO() throws DAOException;
	public abstract SubscriptionDAO getSubscriptionDAO() throws DAOException;
	public abstract PresenceDAO     getPresenceDAO()     throws DAOException;
	
}
