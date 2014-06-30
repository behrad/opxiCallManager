package com.basamadco.opxi.callmanager.entity.dao.directory;

import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.EntityNotExistsException;
import com.basamadco.opxi.callmanager.call.CallTarget;

/**
 * @author Jrad
 *         Date: Jul 26, 2006
 *         Time: 1:58:35 PM
 */
public interface CallTargetDAO extends DirectoryDAO {

    public CallTarget getCallTargetByPhoneNumber( String name ) throws DAOException, EntityNotExistsException ;

    public CallTarget getCallTargetByName( String name ) throws DAOException, EntityNotExistsException ;

    public CallTarget getCallTargetById( String DN ) throws DAOException, EntityNotExistsException ;

    public CallTarget getCallTargetByPattern( String pattern )throws DAOException, EntityNotExistsException ;

}
