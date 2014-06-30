package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 */
public class SubdomainOf extends ConditionalOperator {

    public SubdomainOf( Operand l_opr, Operand r_opr, boolean ignoreCase ) {
        super( l_opr, r_opr, ignoreCase );
    }

    protected boolean checkValues( String requestValue, String groupValue ) {
        if ( requestValue.endsWith( groupValue ) ) {
            int len1 = requestValue.length();
            int len2 = groupValue.length();
            return len1 == len2 || requestValue.charAt( len1 - len2 - 1 ) == '.';
        } else {
            return false;
        }
    }

    protected String getOperatorTypeString() {
        return "subdomain-of";
    }

}