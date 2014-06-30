package com.basamadco.opxi.callmanager;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAOFactory;

/**
 * Type of Call Manager services that need database access.
 *
 * @author Jrad
 *         Date: Apr 8, 2006
 *         Time: 4:04:19 PM
 */
public abstract class DatabaseCallManagerService extends AbstractCallManagerService {

    private DatabaseDAOFactory daof;

/*    protected DatabaseDAOFactory getDAOFactory() throws OpxiException {
        if( daof == null ) {
            daof = BaseDAOFactory.getDatabaseDAOFactory();
        }
        return daof;
    }*/

}