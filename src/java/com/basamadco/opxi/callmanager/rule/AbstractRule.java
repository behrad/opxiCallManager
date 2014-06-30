package com.basamadco.opxi.callmanager.rule;

import com.basamadco.opxi.callmanager.OpxiException;
import com.basamadco.opxi.callmanager.call.CallTarget;
import com.basamadco.opxi.callmanager.entity.profile.Parameter;
import com.basamadco.opxi.callmanager.entity.UserAgent;
import com.basamadco.opxi.callmanager.pool.rules.RuleInstantiationException;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jrad
 *         Date: Sep 14, 2008
 *         Time: 2:48:57 PM
 */
public abstract class AbstractRule implements Rule {

    private static final Logger logger = Logger.getLogger(AbstractRule.class.getName());


    private String targetService;

    private String ruleId;


    private boolean allowed;

    private Class onEvent;

    private int priority;

    private CallTarget targetEntity;


    /**
     * We need a default constructor so that we can instantiate specific
     * subclasses by reflection.
     */
    public AbstractRule() {

        setRuleId(OpxiToolBox.unqualifiedClassName(getClass()) + "" + hashCode());

    }


    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public Class getOnEvent() {
        return onEvent;
    }

    public void setOnEvent(Class onEvent) {
        this.onEvent = onEvent;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTargetService() {
        return targetService;
    }

    public void setTargetService(String targetService) {
        this.targetService = targetService;
    }

    public CallTarget getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(CallTarget targetEntity) {
        this.targetEntity = targetEntity;
    }

    public static AbstractRule instantiate(com.basamadco.opxi.callmanager.entity.profile.Rule ruleSchema,
                                           CallTarget targetEntity) throws RuleInstantiationException {
        try {
            Class ruleClass = Class.forName(ruleSchema.getName());
            ruleClass.asSubclass(AbstractRule.class);
            Object rule = ruleClass.newInstance();
            for (int i = 0; i < ruleSchema.getParameter().length; i++) {
                Parameter parameter = ruleSchema.getParameter()[i];
                OpxiToolBox.invokeSetterMethod(rule, parameter.getName(), parameter.getValue());
            }
            AbstractRule myRule = (AbstractRule) rule;
            if (ruleSchema.getMode().equalsIgnoreCase("allowed")) {
                myRule.setAllowed(true);
            } else {
                myRule.setAllowed(false);
            }
            myRule.setOnEvent(Class.forName(ruleSchema.getOnEvent()));
            myRule.setPriority(ruleSchema.getPriority());
            myRule.setTargetEntity(targetEntity);
//            myRule.setTargetService( targetService );
            return myRule;
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuleInstantiationException(e.getMessage(), e);
        } catch (ClassCastException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuleInstantiationException(e.getMessage(), e);
        } catch (InstantiationException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuleInstantiationException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuleInstantiationException(e.getMessage(), e);
        } catch (OpxiException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuleInstantiationException(e.getMessage(), e);
        }
    }


    public final String toString() {
        return getClass().getName() + ": " + getRuleInfo();
    }

}
