package com.basamadco.opxi.callmanager.pool.rules;

import java.util.List;

/**
 * @author Jrad
 *          Date: Feb 16, 2006
 *          Time: 3:36:24 PM
 */
public class And implements Condition {

    private List m_conditions;

    public And( List conditions ) {
        m_conditions = conditions;
    }

    public boolean evaluate( Object leftOperand, Object rightOperand ) {
        for ( int i = 0; i < m_conditions.size(); i++ )
            if ( !( (Condition) m_conditions.get( i ) ).evaluate( leftOperand, rightOperand ) )
                return false;

        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < m_conditions.size(); i++ ) {
            if ( i > 0 )
                sb.append( " && " );
            sb.append( "(" + m_conditions.get( i ) + ")" );
        }

        return sb.toString();
    }
}