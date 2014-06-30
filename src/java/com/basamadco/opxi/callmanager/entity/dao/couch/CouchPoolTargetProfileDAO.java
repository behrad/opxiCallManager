package com.basamadco.opxi.callmanager.entity.dao.couch;

import com.basamadco.opxi.callmanager.entity.dao.webdav.PoolTargetProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

/**
 * @author Jrad
 *         Date: May 10, 2010
 *         Time: 3:20:57 PM
 */
public class CouchPoolTargetProfileDAO extends com.basamadco.opxi.callmanager.entity.dao.couch.CouchProfileDAO implements PoolTargetProfileDAO {

    public CouchPoolTargetProfileDAO( BaseDAOFactory daof, String owner ) throws DAOException {
        super( daof, owner );
    }
}
