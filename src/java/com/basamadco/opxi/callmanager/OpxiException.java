package com.basamadco.opxi.callmanager;

/**
 * Represents a global error in OpxiCallManager
 * All other Opxi Exception types are subclasses of this class. 
 * 
 * @author Jrad
 *
 */
public class OpxiException extends Exception {
	
	public OpxiException( String msg, Throwable t ) {
		super( msg, t );
	}
	
	public OpxiException( Throwable t ) {
		super( t.getMessage(), t );
	}
	
	public OpxiException( String msg ) {
		super( msg );
	}

}