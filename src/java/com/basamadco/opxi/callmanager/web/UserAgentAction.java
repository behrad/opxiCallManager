package com.basamadco.opxi.callmanager.web;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.dao.BaseDAOFactory;
import com.basamadco.opxi.callmanager.entity.dao.database.DatabaseDAO;

/**
 * @author Jrad
 *         Date: Sep 21, 2006
 *         Time: 11:08:09 AM
 *
 * @struts.action
 *      name="UserAgentForm"
 *      path="/u/agent"
 *      scope="request"
 *      validate="false"
 *      input="pages.useragent.edit"
 *
 *
 * @struts.action
 *      name="UserAgentForm"
 *      path="/r/agent"
 *      scope="request"
 *      validate="false"
 *      input="pages.useragent.read"
 *
 *
 * @struts.action-forward
 *    name="read"
 *    path="pages.useragent.read"
 *
 *
 * @struts.action-forward
 *    name="edit"
 *    path="pages.useragent.edit"
 *
 *
 * @struts.action-forward
 *    name="list"
 *    path="pages.useragent.list"
 *
 */
public class UserAgentAction extends BaseAction {

    protected DatabaseDAO getDAO() throws OpxiException {
//        return BaseDAOFactory.getDatabaseDAOFactory().getUserAgentDAO();
        throw new IllegalStateException( "Not Implemented!" );
    }



}
