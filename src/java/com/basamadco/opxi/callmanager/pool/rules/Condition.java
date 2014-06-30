package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 *         Date: Feb 16, 2006
 *         Time: 3:05:19 PM
 */
public interface Condition {

    public boolean evaluate( Object leftOperand, Object rightOperand );

}