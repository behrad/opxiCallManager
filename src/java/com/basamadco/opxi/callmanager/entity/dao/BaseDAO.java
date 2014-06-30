package com.basamadco.opxi.callmanager.entity.dao;

/**
 * Data Access Object pattern base view
 * 
 * @author Jrad
 *
 */
public interface BaseDAO {
	
	/**
	 * Returns current associated DAO Factory
	 * @return DatabaseDAOFactory object
	 */
	public BaseDAOFactory getDAOFactory();
	
}