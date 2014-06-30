package com.basamadco.opxi.callmanager.entity.dao.database;

import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.entity.ValueObject;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAO;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

import java.util.List;

/**
 * DatabaseDAO is the common interface of all opxiCallManager entities
 * 
 * @author Jrad
 *
 */
public interface DatabaseDAO extends BaseDAO {
		
	public ValueObject load( Class clazz, Long id ) throws DAOException;
	
	public String   save( ValueObject data )    	throws DAOException;
	
	public void update( ValueObject data ) 	throws DAOException;
	
	public void delete( ValueObject data ) 	throws DAOException;

//    public ValueObject readByName( String name ) 	throws DAOException, EntityNotExistsException;

//    public Object dynaRead( String id, String value ) throws DAOException, EntityNotExistsException;

    public List listAll() throws DAOException;

    public String getNameFieldName();

    public Class getValueObjectClass();
}
