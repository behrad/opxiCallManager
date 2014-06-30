package com.basamadco.opxi.callmanager.pool;

import com.basamadco.opxi.callmanager.OpxiException;

public class NoIdleAgentException extends OpxiException {
	
	public NoIdleAgentException( String msg ) {
		super( msg );
	}

}
