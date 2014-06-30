<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory" %>
<%@ page import="com.basamadco.opxi.callmanager.pool.AgentPool" %>
<%@ page import="com.basamadco.opxi.callmanager.rule.AbstractRule" %>
<%@ page import="java.util.List" %>
<% try { %>


<table align="center" class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td class="titrecon">#</td>
        <td class="titrecon">Rule Info</td>
        <td class="titrecon">Usages</td>
    </tr>

    <%
        //Collection messages = (Collection)request.getAttribute( "queueList" );
        String queueName = request.getParameter("id");
        ServiceFactory sf = ((ServiceFactory) application.getAttribute("com.basamadco.opxi.callmanager.ServiceFactory"));
        AgentPool pool = sf.getPoolService().getPool(queueName);

        List rules = pool.getRules();
        for (int i = 0; i < rules.size(); i++) {
            AbstractRule rule = (AbstractRule) rules.get(i);
    %>
    <tr>
        <td class="rowdef"><%=i + 1%>
        </td>
        <td class="rowdef"><%= rule.getRuleInfo() %>
        </td>
        <td class="rowdef">
            <table align="center" class="mainCon" cellpadding="0" cellspacing="0">
                <%
                    List ctxs = rule.getEventContexts();
                    for (int j = 0; j < ctxs.size(); j++) {
                        Object ctx = ctxs.get(j);
                %>
                <tr>
                    <td class="rowdef">
                        <%= ctx %>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
        </td>
    </tr>
    <%
        }
    %>


</table>


<%
} catch (Exception e) {
%>
<div align="center">Error Message:<p class="meg">&nbsp;<%=e%>
</p></div>
<%
    }
%>