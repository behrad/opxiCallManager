<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.Registration" %>
<%@ page import="com.basamadco.opxi.callmanager.logging.AgentActivityLogger" %>
<%@ page import="com.basamadco.opxi.callmanager.pool.Agent" %>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<% try { %>

<p>List of registered contacts in OPXI Call Manager</p>

<%
    ServiceFactory sf = ( (ServiceFactory) application.getAttribute( "com.basamadco.opxi.callmanager.ServiceFactory" ) );
    Collection registrations = sf.getLocationService().getAllRegistrations();
%>
<table class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td class="titrecon">#</td>
        <td class="titrecon">Phone #</td>
        <td class="titrecon">Contact Address</td>
        <td class="titrecon">Expiry</td>
        <td class="titrecon">Comment</td>
        <td class="titrecon">Ref. Context Ids</td>
        <td class="titrecon">Activity Log</td>
    </tr>
    <%
        int count = 1;
        Iterator it = registrations.iterator();
        while( it.hasNext() ) {
            Registration contact = (Registration)it.next();
            Agent agent = sf.getAgentService().getAgentForUA( contact.getUserAgent() );
            try {
    %>
<tr>
        <td class="rowdef"><%=count++%>
        </td>
        <td class="rowdef"><%=agent.getTelephoneNumber()%>
        </td>
        <td class="rowdef"><%=OpxiToolBox.escapeForHTML( contact.getLocation().toString() )%>
        </td>

        <td class="rowdef"><%=OpxiToolBox.duration( contact.getExpiry().getTime() - System.currentTimeMillis() ) %>
        </td>
        <td class="rowdef"><%=contact.getComment()%>
        </td>
        <td class="rowdef"><%
            for( int i = 0; i < sf.getLocationService().getAssociatedContextIds( contact ).length; i++ ) {
        %>

                <%= sf.getLocationService().getAssociatedContextIds( contact )[ i ] + ", " %>
        <%
            }
        %>
        </td>
        <td class="rowdef" align="center"><a target="_blank"
                                             href="<%=request.getContextPath()%>/log?type=<%=AgentActivityLogger.class.getName()%>&logVOId=<%=agent.getActivityLogId()%>">view</a>
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