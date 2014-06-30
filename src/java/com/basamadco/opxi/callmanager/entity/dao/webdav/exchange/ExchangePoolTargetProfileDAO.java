package com.basamadco.opxi.callmanager.entity.dao.webdav.exchange;

import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.webdav.PoolTargetProfileDAO;

/**
 * @author Jrad
 *         Date: Oct 4, 2006
 *         Time: 1:40:30 PM
 */
public class ExchangePoolTargetProfileDAO extends ExchangeProfileDAO implements PoolTargetProfileDAO {

    public ExchangePoolTargetProfileDAO( BaseDAOFactory daof, String profileURI ) {
        super( daof, profileURI );
    }


}