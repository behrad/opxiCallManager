package com.basamadco.opxi.callmanager.pool.rules;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.entity.profile.Parameter;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Subclasses should provide 2 types of information in their implementation:
 *  1) A public setter method for each defined property in rule schema with just one String parameter  
 *  2) Override evaluate method
 * 
 * Note: All readable properties MUST have a setter method with a java.lang.String argument
 *
 * @author Jrad
 *         Date: Oct 28, 2006
 *         Time: 3:16:50 PM
 *
 */
public abstract class AbstractMatchingRule implements MatchingRule {

    private static final Logger logger = Logger.getLogger( AbstractMatchingRule.class.getName() );


    /**
     * We need a default constructor so that we can instantiate specific
     * subclasses by reflection.
     */
    public AbstractMatchingRule() {
    }

    public final String toString() {
        return getClass().getName() + ": " + getRuleInfo();
    }

    /**
     * This method will help SkillBasedPools to instantiate their
     * conceret MatchingRule types from their profile
     *
     * @param ruleSchema MatchingRule element of SkillProfile in profile schema
     * @return Specific MatchingRule
     */
    public static AbstractMatchingRule instantiate(
            com.basamadco.opxi.callmanager.entity.profile.MatchingRule ruleSchema ) throws RuleInstantiationException {
        try {
            Class matchingRuleClass = Class.forName( ruleSchema.getClassName() );
            matchingRuleClass.asSubclass( AbstractMatchingRule.class );
            Object matchingRule = matchingRuleClass.newInstance();
            for (int i = 0; i < ruleSchema.getParameter().length; i++) {
                Parameter parameter = ruleSchema.getParameter()[i];
                OpxiToolBox.invokeSetterMethod( matchingRule, parameter.getName(), parameter.getValue() );
            }
            return (AbstractMatchingRule)matchingRule;
        } catch ( ClassNotFoundException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new RuleInstantiationException( e.getMessage(), e );
        } catch ( ClassCastException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new RuleInstantiationException( e.getMessage(), e );
        } catch ( InstantiationException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new RuleInstantiationException( e.getMessage(), e );
        } catch ( IllegalAccessException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new RuleInstantiationException( e.getMessage(), e );
        } catch ( OpxiException e ) {
            logger.log( Level.SEVERE, e.getMessage(), e );
            throw new RuleInstantiationException( e.getMessage(), e );
        }
    }

}