package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.DAOException;
import com.basamadco.opxi.callmanager.entity.dao.webdav.AgentProfileDAO;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 1:41:16 PM
 */
public class ExchangeAgentProfileDAO extends ExchangeProfileDAO implements AgentProfileDAO {

    public ExchangeAgentProfileDAO( BaseDAOFactory daof, String userName, String path ) throws DAOException {
        super( daof, userName, path );
    }

}
