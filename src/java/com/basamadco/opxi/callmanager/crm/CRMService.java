package com.basamadco.opxi.callmanager.crm;

import com.basamadco.opxi.callmanager.AbstractCallManagerService;
import com.basamadco.opxi.crm.CRMAccessModule;

/**
 * @author Jrad
 *         Date: Oct 7, 2006
 *         Time: 3:37:57 PM
 */
public abstract class CRMService extends AbstractCallManagerService {

    public abstract CRMAccessModule getModule();
    
}