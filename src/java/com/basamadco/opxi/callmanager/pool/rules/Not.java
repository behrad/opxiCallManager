package com.basamadco.opxi.callmanager.pool.rules;

/**
 * @author Jrad
 */
public class Not implements Condition {

    protected Condition m_cond;

    public Not( Condition cond ) {
        m_cond = cond;
    }

    public boolean evaluate( Object leftOperand, Object rightOperand ) {
        return !m_cond.evaluate( leftOperand, rightOperand );
    }

    public String toString() {
        return "! " + m_cond;
    }
}
