package com.basamadco.opxi.callmanager.rule;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.UserAgent;

import java.util.List;

/**
 * Root hierarchy of rules in Opxi Call Manager. Rules are processing units which work as filters.
 * Different processing rules can be defined to handle specific conditional service logics for each
 * and every Opxi Call Manager service component.
 *
 * @author Jrad
 *         Date: Sep 14, 2008
 *         Time: 2:42:16 PM
 */
public interface Rule {

    /**
     * This method should implement the rule logic to check if this rule is satisfied or not
     * Rule implementations should check if they are responsible for the event type and return
     * false if event is against this rule, otherwise they should return true to let other rules
     * to judge this event in the rule chain. If this rule is not responsible for the input event
     * type it should throw a RuleNotInvolvedException
     *
     * @param event The input object on which processing is triggered
     * @return false if the input event is against this rule, otherwise true
     * @throws RuleNotInvolvedException, if this rule is not responsible for the input event
     * @throws OpxiException,            if any error occurs
     */
    boolean evaluate( Object event ) throws OpxiException;

    /**
     * A short description of this rule
     *
     * @return Rule description string
     */
    String getRuleInfo();


    String getRuleId();


    List getEventContexts();


    RuleUsage getHistory( UserAgent userAgent );


//    boolean isActive();

}
