package com.basamadco.opxi.callmanager.call;

import com.basamadco.opxi.callmanager.call.Leg;
import com.basamadco.opxi.callmanager.SipService;

import javax.servlet.sip.SipServletRequest;

/**
 * @author Jrad
 *         Date: Aug 7, 2006
 *         Time: 12:08:44 PM
 */
public class UniLegPlaceHolder extends Leg {

    public static final UniLegPlaceHolder INSTANCE = new UniLegPlaceHolder();


    protected UniLegPlaceHolder() {
        super( null, null, null, "UniLegPlaceHolder" );
        state = com.basamadco.opxi.callmanager.call.LegState.IDLE;
    }

}
