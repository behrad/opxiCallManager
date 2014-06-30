package com.basamadco.opxi.callmanager.entity;


/**
 * This Exception is thrown by the OpxiCallManger when no user agent information
 * is exists for some Address of Record.
 *  
 * @author Jrad
 *
 */
public class UserNotAvailableException extends RegistrationNotAvailableException {
	
	public UserNotAvailableException( String msg ) {
		super( msg );
	}

}
