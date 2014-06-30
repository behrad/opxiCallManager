package com.basamadco.opxi.callmanager.sip;

import com.basamadco.opxi.callmanager.sip.util.SIPConstants;

import java.io.Serializable;

/**
 * Represents a context being involved with a SIP Application Session in Call Manager
 *  
 * @author Jrad
 *         Date: Jan 15, 2008
 *         Time: 1:02:16 PM
 */
public interface AbstractContext extends Serializable, SIPConstants {

    /**
     * Cleans up context's resources and resources in SIP Application Session related to this context
     */
    public void destroy();

}
