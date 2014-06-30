package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 12:06:31 PM
 */
public class OperandFactory {

    private static final String REQUEST_ATTRIBUTE = "request";

    private static final String GROUP_ATTRIBUTE = "group";

    private static final String LITERAL_ATTRIBUTE = "literal";

    public static Operand getOperand( String name, String attr_name ) throws NoSuchOperandException {
        if( name.equals( REQUEST_ATTRIBUTE ) ) {
            return new RequestOperand( attr_name );
        } else if ( name.equals( GROUP_ATTRIBUTE ) ) {
            return new GroupOperand( attr_name );
        } else if( name.equals( LITERAL_ATTRIBUTE ) ) {
            return new LiteralOperand( attr_name );
        }
        throw new NoSuchOperandException( name );
    }

}
