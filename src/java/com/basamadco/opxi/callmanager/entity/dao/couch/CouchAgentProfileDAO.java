package com.basamadco.opxi.callmanager.entity.dao.couch;

import com.basamadco.opxi.callmanager.entity.dao.webdav.AgentProfileDAO;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;

/**
 * @author Jrad
 *         Date: May 10, 2010
 *         Time: 3:19:55 PM
 */
public class CouchAgentProfileDAO extends com.basamadco.opxi.callmanager.entity.dao.couch.CouchProfileDAO implements AgentProfileDAO {

    public CouchAgentProfileDAO( BaseDAOFactory daof, String owner ) throws DAOException {
        super( daof, owner );
    }
}
