package com.basamadco.opxi.callmanager.sip.security;

import com.basamadco.opxi.callmanager.OpxiException;

/**
 * Represents any error that may happen during the process
 * of initializing a DigestResponse wrapper for the client
 * Authorization response header.
 * 
 * This Exception may contain the underlying caused error as
 * a Throwable object.
 * 
 * @author Jrad
 */
public class DigestResponseException extends OpxiException {

	public DigestResponseException( String msg ) {
		super( msg );
	}
	
	public DigestResponseException( Throwable t ) {
		super( t );
	}
}
