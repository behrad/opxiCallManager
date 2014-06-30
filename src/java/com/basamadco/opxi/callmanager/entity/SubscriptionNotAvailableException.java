package com.basamadco.opxi.callmanager.entity;

import com.basamadco.opxi.callmanager.OpxiException;


public class SubscriptionNotAvailableException extends OpxiException {

	public SubscriptionNotAvailableException( String msg ) {
		super(msg);
	}
}
