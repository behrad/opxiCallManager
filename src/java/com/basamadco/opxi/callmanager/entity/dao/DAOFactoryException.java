package com.basamadco.opxi.callmanager.entity.dao;

import com.basamadco.opxi.callmanager.OpxiException;

public class DAOFactoryException extends OpxiException {

    public DAOFactoryException( String msg ) {
        super( msg );
    }

    public DAOFactoryException( String msg, Throwable t ) {
        super( msg, t );
    }

}
