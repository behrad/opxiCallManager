package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOFactoryException;
import com.basamadco.opxi.callmanager.entity.dao.directory.DirectoryDAOFactory;

/**
 * Type of Call Manager service that needs access directory server.
 *
 * @author Jrad
 *         Date: Apr 8, 2006
 *         Time: 4:09:51 PM
 */
public abstract class DirectoryCallManagerService extends AbstractCallManagerService {

    private DirectoryDAOFactory daof;

    protected DirectoryDAOFactory getDAOFactory() {
        if( daof == null ) {
            try {
                daof = BaseDAOFactory.getDirectoryDAOFactory();
            } catch ( DAOFactoryException e ) {
                throw new RuntimeException( e.getMessage(), e );
            }
        }
        return daof;
    }

}