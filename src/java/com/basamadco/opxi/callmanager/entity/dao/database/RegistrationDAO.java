package com.basamadco.opxi.callmanager.entity.dao.database;

import com.basamadco.opxi.callmanager.entity.Registration;
import com.basamadco.opxi.callmanager.entity.RegistrationNotAvailableException;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

import java.util.List;

public interface RegistrationDAO extends DatabaseDAO {
	
	public List find( UserAgent ua ) throws DAOException;
	
	public Registration find( UserAgent ua, String location ) throws DAOException, RegistrationNotAvailableException;
	
//	public Registration find( String location ) throws DAOException, RegistrationNotAvailableException;
	
	public void deleteRegistrations( UserAgent ua ) throws DAOException;

}
