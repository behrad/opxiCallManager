<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.Presence" %>
<%@ page import="com.basamadco.opxi.callmanager.pool.Agent" %>
<%@ page import="com.basamadco.opxi.callmanager.sip.presence.PublishContext" %>
<%@ page import="com.basamadco.opxi.callmanager.sip.util.SIPConstants" %>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.UserAgent" %>
<% try { %>

<p>List of online queue service agents</p>

<%
    //Collection messages = (Collection)request.getAttribute( "queueList" );
    ServiceFactory sf = ((ServiceFactory) application.getAttribute( "com.basamadco.opxi.callmanager.ServiceFactory" ));
    Collection agents = sf.getPoolService().listPoolAgents();

%>
<table class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td class="titrecon">#</td>
        <td class="titrecon">AOR</td>
        <td class="titrecon">S. Status</td>
        <td class="titrecon">Open Calls</td>
        <td class="titrecon">Idle/Rest Time</td>
        <td class="titrecon">Contact</td>
        <td class="titrecon">P. Status</td>
        <td class="titrecon">Online/Offline</td>
        <td class="titrecon">Presence Rules Usage</td>
        <td class="titrecon">Pools</td>
        <!--<td class="titrecon">pool type</td>-->
    </tr>
    <%
        int count = 1;
        Iterator it = agents.iterator();
        while ( it.hasNext() ) {
            Agent agent = (Agent) it.next();
            try {
                Presence status = sf.getPresenceService().getPresence( new UserAgent( agent.getAOR() ) );
                PublishContext ctx = sf.getPresenceService().getActivePresenceContext( status.getUserAgent() );

    %>
    <tr>
        <td class="rowdef"><%=count++%>
        </td>
        <td class="rowdef"><%=agent.getAOR()%>
        </td>
        <td class="rowdef"><font color="<%= agent.isIdle() ? "blue\">" + agent.stateString() : "red\">" + agent.stateString() %></font>
        </td>
        <td class=" rowdef"><%=agent.openCalls()%>
        </td>
        <td class="rowdef"><%=OpxiToolBox.duration( agent.idleTime() )%>
        </td>
        <td class="rowdef"><%=status.getLocation().getURI()%>
        </td>
        <td class="rowdef" align="center"><font color="green"><%= status.getNote() %>
        </font>
        </td>
        <td class="rowdef" align="center"><%=
        (SIPConstants.BASIC_STATUS_OPEN.equalsIgnoreCase( status.getBasic() ) ? "Online" : "<font color=\"red\">Offline</font>")%>
        </td>
        <td class="rowdef" align="center"><%
            if ( ctx.getUsageCtx() == null ) {
        %>
            ---
            <%
            } else {
            %>
            <a href="<%=request.getContextPath()%>/apu-show.jsp?ua=<%=agent.getAOR()%>">view</a>
            <%
                }
            %>
        </td>
        <td class="rowdef" align="center"><%=agent.getPoolMemberships()%>
        </td>

    </tr>
    <%
            } catch ( Exception e ) {

            }
        }
    %>
</table>
<%
} catch ( Exception e ) {
%>
<div align="center">Error Message:<p class="meg">&nbsp;<%=e %>
    <%
        e.printStackTrace();
    %>
</p></div>
<%
    }
%>