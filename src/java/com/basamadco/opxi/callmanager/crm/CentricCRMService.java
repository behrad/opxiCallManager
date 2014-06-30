package com.basamadco.opxi.callmanager.crm;

import com.basamadco.opxi.callmanager.util.PropertyUtil;
import com.basamadco.opxi.crm.CRMAccessModule;
import com.basamadco.opxi.crm.OpxiCRMFactory;

import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Oct 8, 2006
 *         Time: 4:36:15 PM
 */
public class CentricCRMService extends CRMService {

    private static final Logger logger = Logger.getLogger( CentricCRMService.class.getName() );

    private static final String myurl = PropertyUtil.getProperty( "opxi.callmanager.crm.customerPageUrl" );

    private CRMAccessModule crmModule;

    public CentricCRMService() {
        crmModule = OpxiCRMFactory.getCRMAccessModule( OpxiCRMFactory.CENTRIC_CRM );
    }

    public CRMAccessModule getModule() {
        return crmModule;
    }
}


