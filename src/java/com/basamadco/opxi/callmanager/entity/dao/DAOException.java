package com.basamadco.opxi.callmanager.entity.dao;

import com.basamadco.opxi.callmanager.OpxiException;

public class DAOException extends OpxiException {

    public static final int DIR_ALREADY_EXISTS = 405;
    

    private int errorCode;

    public DAOException( String msg ) {
		super( msg );
	}

    public DAOException( String msg, int errorCode ) {
		super( msg );
        this.errorCode = errorCode;
    }

    public DAOException( String msg, Throwable t ) {
        super( msg, t );
    }

    public DAOException( String msg, Throwable t, int errorCode ) {
        super( msg, t );
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
