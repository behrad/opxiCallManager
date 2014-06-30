package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 *         Date: Feb 16, 2006
 *         Time: 3:36:24 PM
 */
public class Contains extends ConditionalOperator {

    public Contains( Operand l_opr, Operand r_opr, boolean ignoreCase ) {
        super( l_opr, r_opr, ignoreCase );
    }

    protected boolean checkValues( String requestValue, String groupValue ) {
        return requestValue.indexOf( groupValue ) > -1;
    }

    protected String getOperatorTypeString() {
        return "contains";
    }
}