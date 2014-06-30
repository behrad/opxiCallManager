package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 */
public class Operator {

    protected boolean ignoreCase;

    protected Operand l_opr;

    protected Operand r_opr;

    protected Operator( Operand l_opr, Operand r_opr, boolean ignoreCase ) {
//        if( reqVarName == null ) {
//            throw new IllegalArgumentException( "Request attribute name must be non-null" );
//        }
//        if ( groupVarName == null ) {
//            throw new IllegalArgumentException( "Group attribute name must be non-null" );
//        }
        this.ignoreCase = ignoreCase;
        this.l_opr = l_opr;
        this.r_opr = r_opr;
    }

    protected String getLeftOperandValue( Object object ) {
//        if( object == null ) { // TODO remove the if (just for debug)
//            return "Group1";
//        }
        return l_opr.bind( object );
    }

    protected String getRightOperandValue( Object object ) {
        return r_opr.bind( object );
    }

    protected String getLeftOperandName() {
        return l_opr.getName();
    }

    protected String getRightOperandName() {
        return r_opr.getName();
    }

}
