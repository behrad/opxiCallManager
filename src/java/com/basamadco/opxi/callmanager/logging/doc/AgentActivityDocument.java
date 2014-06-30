package com.basamadco.opxi.callmanager.logging.doc;

import org.svenson.JSONProperty;

/**
 * @author Jrad
 *         Date: May 7, 2010
 *         Time: 12:18:59 PM
 */
public class AgentActivityDocument extends LogDocument {

    private String agentName;

    private static final String AGENT_ACTIVITY_LOG = "agentActivityLog";
    

    public AgentActivityDocument() {
        setType( AGENT_ACTIVITY_LOG );
    }

    @JSONProperty
    public String getAgentName() {
        return agentName;
    }

    public void setAgentName( String agentName ) {
        this.agentName = agentName;
    }
}
