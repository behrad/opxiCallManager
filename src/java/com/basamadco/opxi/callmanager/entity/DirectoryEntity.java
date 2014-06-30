package com.basamadco.opxi.callmanager.entity;

import com.basamadco.opxi.callmanager.entity.dao.directory.ldap.LdapDAO;
import com.basamadco.opxi.callmanager.util.OpxiToolBox;

import java.io.Serializable;

/**
 * @author Jrad
 *         Date: Mar 5, 2006
 *         Time: 2:39:44 PM
 */
public class DirectoryEntity implements Serializable {

    private String CN;

    private String DN;

    private boolean isGroup;

    private boolean isSkill;

    private boolean isAgent;

    private boolean isApplication;

    private boolean isTrunk;

    private int typeStringIndex;

    private String homeURI;

    private static final String[] typeStrings = {"undefined", "Group", "Skill", "Agent", "Application", "Trunk"};

    public DirectoryEntity() {
    }

    public void setCN(String CN) {
        this.CN = CN;
    }

    public String getCN() {
        return CN;
    }

    public String getDN() {
        return DN;
    }

    public void setDN(String DN) {
        this.DN = DN;
        setType(DN);
    }


    public String getHomeURI() {
        return homeURI;
    }

    public void setHomeURI(String homeURI) {
        this.homeURI = homeURI;
    }

    private void setAsGroup() {
        typeStringIndex = 1;
        isGroup = true;
        isSkill = false;
        isApplication = false;
        isAgent = false;
        isTrunk = false;
    }

    private void setAsSkill() {
        typeStringIndex = 2;
        isSkill = true;
        isGroup = false;
        isApplication = false;
        isAgent = false;
        isTrunk = false;
    }

    private void setAsAgent() {
        typeStringIndex = 3;
        isAgent = true;
        isSkill = false;
        isApplication = false;
        isGroup = false;
        isTrunk = false;
    }

    private void setAsApplication() {
        typeStringIndex = 4;
        isApplication = true;
        isSkill = false;
        isGroup = false;
        isAgent = false;
        isTrunk = false;
    }

    private void setAsTrunk() {
        typeStringIndex = 5;
        isApplication = false;
        isSkill = false;
        isGroup = false;
        isAgent = false;
        isTrunk = true;
    }

    private void setType(String dn) {
        // TODO should be changed to a more suffisticated algorithm based on objectClass
        if (dn.indexOf(LdapDAO.AGENT_TREE) > 0) {
            setAsAgent();
        } else if (dn.indexOf(LdapDAO.SKILL_TREE) > 0) {
            setAsSkill();
        } else if (dn.indexOf(LdapDAO.GROUP_TREE) > 0) {
            setAsGroup();
        } else if (dn.indexOf(LdapDAO.TRUNK_TREE) > 0) {
            setAsTrunk();
        } else {
            setAsApplication();
        }
    }

    public boolean isGroup() {
        return isGroup;
    }

    public boolean isAgent() {
        return isAgent;
    }

    public boolean isSkill() {
        return isSkill;
    }

    public boolean isApplication() {
        return isApplication;
    }

    public boolean isTrunk() {
        return isTrunk;
    }

    public String toString() {
        return OpxiToolBox.unqualifiedClassName(getClass()) +
                "[DN='" + DN + "', type='" + typeStrings[typeStringIndex] + "']";
    }

}