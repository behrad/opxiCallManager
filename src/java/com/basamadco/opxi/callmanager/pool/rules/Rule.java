package com.basamadco.opxi.callmanager.pool.rules;

import com.basamadco.opxi.callmanager.pool.PoolTarget;

/**
 * @author Jrad
 *         Date: Feb 16, 2006
 *         Time: 3:04:14 PM
 */
public class Rule {

    protected PoolTarget owner;

    protected Condition m_cond;

    public Rule( PoolTarget owner, Condition cond ) {
        this.owner = owner;
        m_cond = cond;
    }

    public String getName() {
        return owner.getName();
    }

    public String getConditionType() {
        return m_cond.toString();
    }

    public boolean evaluate( Object rightOperand /*SipServletRequest req*/ ) {
        return m_cond.evaluate( owner, rightOperand );
    }

    public boolean equals( Object o ) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final Rule rule = (Rule) o;
        return !( owner != null ? !owner.equals( rule.owner ) : rule.owner != null );
    }

    public int hashCode() {
        return ( owner != null ? owner.hashCode() : 0 );
    }

    public static Rule parse( String xml, PoolTarget pool ) throws Exception {
        try {
            return MatchingRuleParser.parse( xml, pool );
        } catch ( Exception e ) {
            throw e;
        }
    }
}