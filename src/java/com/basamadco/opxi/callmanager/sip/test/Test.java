package com.basamadco.opxi.callmanager.sip.test;

/**
 * 
 * @author Jrad
 *
 */
public abstract class Test implements Runnable {

    public boolean enabled() {
		return enabled;
	}

    private boolean enabled = false;

    protected void setEnabled() {
        enabled = true;
    }

}
