package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 *         Date: Feb 16, 2006
 *         Time: 3:30:27 PM
 */
public abstract class ConditionalOperator extends Operator implements Condition {

    protected ConditionalOperator( Operand l_opr, Operand r_opr, boolean ignoreCase ) {
        super( l_opr, r_opr, ignoreCase );
    }

    public boolean evaluate( Object leftOperand, Object rightOperand ) {
        String l_Val = getLeftOperandValue( leftOperand );
        String r_Val = getRightOperandValue( rightOperand );

        if ( l_Val == null ) {
            return false;
        }
        if ( r_Val == null ) {
            return false;
        }
        if ( ignoreCase ) {
            l_Val = l_Val.toLowerCase();
            r_Val = r_Val.toLowerCase();
        }
        return checkValues( l_Val, r_Val );
    }

    public String toString() {
        return l_opr.getName() + "." + l_opr.getAttributeName() +
                " " + getOperatorTypeString() + " " +
                r_opr.getName() + "." + r_opr.getAttributeName();
    }

    protected abstract boolean checkValues( String requestValue, String groupValue );

    protected abstract String getOperatorTypeString();

}
