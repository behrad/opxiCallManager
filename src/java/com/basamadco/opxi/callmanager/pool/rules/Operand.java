package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 11:40:15 AM
 */
public interface Operand {

    public String getName();

    public String getAttributeName();

    public String bind( Object o );

}
