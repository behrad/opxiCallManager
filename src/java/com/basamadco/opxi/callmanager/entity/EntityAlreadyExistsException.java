package com.basamadco.opxi.callmanager.entity;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

/**
 * @author Jrad
 *         Date: Jan 16, 2008
 *         Time: 4:25:29 PM
 */
public class EntityAlreadyExistsException extends OpxiException {

    public EntityAlreadyExistsException( ValueObject entity ) {
        super( OpxiToolBox.unqualifiedClassName( entity.getClass() ) + " already exists with Id '" + entity.getId() + "'" );
    }

}
