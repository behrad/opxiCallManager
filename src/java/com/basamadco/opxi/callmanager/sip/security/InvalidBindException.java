package com.basamadco.opxi.callmanager.sip.security;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * Indicates an error that causes a bind operation to be
 * unsuccessful.
 * This class represents invalid credentials error. 
 * 
 * @author Jrad
 *
 */
public class InvalidBindException extends OpxiException {
	
	public InvalidBindException( String msg ) {
		super( msg );
	}

	
}
