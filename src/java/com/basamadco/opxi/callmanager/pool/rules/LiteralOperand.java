package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 12:46:56 PM
 */
public class LiteralOperand implements Operand {

    private String value;

    public LiteralOperand( String value ) {
        this.value = value;
    }

    public String getName() {
        return "literal";
    }

    public String getAttributeName() {
        return value;
    }

    public String bind( Object o ) {
        return value;
    }

}
