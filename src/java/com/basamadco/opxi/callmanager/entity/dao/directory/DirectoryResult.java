package com.basamadco.opxi.callmanager.entity.dao.directory;

/**
 * @author Jrad
 *         Date: Apr 4, 2006
 *         Time: 3:45:45 PM
 */
public class DirectoryResult {

    private String cn;

    private boolean isAgent;

    private boolean isGroup;

    public DirectoryResult( String cn ) {
        this.cn = cn;
    }

    public String getCN() {
        return cn;
    }

    public void setAgent() {
        isAgent = true;
    }

    public void setGroup() {
        isGroup = true;
    }

    public boolean isAgent() {
        return isAgent;
    }

    public boolean isGroup() {
        return isGroup;
    }

}