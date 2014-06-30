package com.basamadco.opxi.callmanager.web;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jrad
 *         Date: Sep 21, 2006
 *         Time: 10:47:40 AM
 */
public class BaseActionContext {

    private DynaActionForm form;

    private HttpServletRequest request;

    private String action;

    private String subAction;

    private ActionMapping actionMapping;

    private String valueObjectClassName;


    public BaseActionContext( DynaActionForm form, HttpServletRequest request, ActionMapping actMap, Class aClass) {
        this.form = form;
        this.request = request;
        this.actionMapping = actMap;
        this.valueObjectClassName = aClass.getName();
        this.action = (String)form.get( BaseAction.ACTION_TYPE );
    }

    public String getValueObjectClassName() {
        return valueObjectClassName;
    }

    public void setValueObjectClassName(String valueObjectClassName) {
        this.valueObjectClassName = valueObjectClassName;
    }

    public ActionMapping getActionMapping() {
        return actionMapping;
    }

    public void setActionMapping(ActionMapping actionMapping) {
        this.actionMapping = actionMapping;
    }

    public DynaActionForm getForm() {
        return form;
    }

    public void setForm(DynaActionForm form) {
        this.form = form;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getSubAction() {
        return subAction;
    }

    public void setSubAction(String subAction) {
        this.subAction = subAction;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
