package com.basamadco.opxi.callmanager.entity.dao.database;

import com.basamadco.opxi.callmanager.entity.Domain;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;


public interface DomainDAO extends DatabaseDAO {

	public Domain findOrSave( String name ) throws DAOException;

}