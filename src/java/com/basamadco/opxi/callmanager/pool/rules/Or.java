package com.basamadco.opxi.callmanager.pool.rules;

import java.util.List;

/**
 * @author Jrad
 */
public class Or implements Condition {

    private List m_conditions;

    public Or( List conditions ) {
        m_conditions = conditions;
    }

    public boolean evaluate( Object leftOperand, Object rightOperand ) {
        for (int i = 0; i < m_conditions.size(); i++)
            if (( (Condition) m_conditions.get( i ) ).evaluate( leftOperand, rightOperand ))
                return true;

        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < m_conditions.size(); i++) {
            if (i > 0)
                sb.append( " || " );
            sb.append( "(" + m_conditions.get( i ) + ")" );
        }

        return sb.toString();
    }
}