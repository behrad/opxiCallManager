package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 */
public class Equal extends ConditionalOperator {

    public Equal( Operand l_opr, Operand r_opr, boolean ignoreCase ) {
        super( l_opr, r_opr, ignoreCase );
    }

    protected boolean checkValues( String requestValue, String groupValue ) {
        return requestValue.equals( groupValue );
    }

    protected String getOperatorTypeString() {
        return "==";
    }

}
