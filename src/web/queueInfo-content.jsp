<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.profile.*" %>
<%@ page import="com.basamadco.opxi.callmanager.pool.AgentPool" %>
<% try { %>
<%
    String queueName = request.getParameter("id");
    ServiceFactory sf = ((ServiceFactory) application.getAttribute("com.basamadco.opxi.callmanager.ServiceFactory"));
    AgentPool pool = sf.getPoolService().getPool(queueName);
%>

<p class="title">Queue Profile Information for queue: <%= pool.getName() %>
</p>
<table align="center" class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td colspan="5" class="titrecon">Application Integration</td>
    </tr>
    <tr>
        <td class="titrecon">Count</td>
        <td class="titrecon">Name</td>
        <td class="titrecon">Expression</td>
        <td class="titrecon">Participants</td>
        <td class="titrecon">Parameters</td>
    </tr>

    <%

        for (int i = 0; i < pool.getProfile().getApplicationIntegration().getApplicationCount(); i++) {
            Application app = pool.getProfile().getApplicationIntegration().getApplication(i);
    %>
    <tr>
        <td class="rowdef"><%= i + 1 %>
        </td>
        <td class="rowdef">
            <%= app.getName() %>
        </td>
        <td class="rowdef">
            <%= app.getExpression() %>
        </td>
        <td valign="top">


            <!-- Participation Table -->
            <table height="100%" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="titrecon">Party</td>
                    <td class="titrecon">Role</td>
                </tr>
                <%
                    for (int j = 0; j < app.getParticipationCount(); j++) {
                        Participation p = app.getParticipation(j);
                %>
                <tr>
                    <td class="rowdef">
                        <%= p.getParty() %>
                    </td>
                    <td class="rowdef">
                        <%= p.getRole() %>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>


        </td>


        <td valign="top">


            <!-- Participation Table -->
            <table height="100%" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="titrecon">Name</td>
                    <td class="titrecon">Value</td>
                </tr>
                <%
                    for (int k = 0; k < app.getParameterCount(); k++) {
                        Parameter p = app.getParameter(k);
                %>
                <tr>
                    <td class="rowdef">
                        <%= p.getName() %>
                    </td>
                    <td class="rowdef">
                        <%= p.getValue() %>
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

</br>

<table align="center" class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td colspan="7" class="titrecon">Rule Set</td>
    </tr>
    <tr>
        <td class="titrecon">Count</td>
        <td class="titrecon">Name</td>
        <td class="titrecon">Event</td>
        <td class="titrecon">Mode</td>
        <td class="titrecon">TargetService</td>
        <td class="titrecon">Priority</td>
        <td class="titrecon">Parameters</td>
    </tr>

    <%
        int k = 1;
        for (int i = 0; i < pool.getProfile().getRuleSetCount(); i++) {
            RuleSet ruleSet = pool.getProfile().getRuleSet(i);
            for (int j = 0; j < ruleSet.getRuleCount(); j++) {
                Rule rule = ruleSet.getRule(j);

    %>
    <tr>
        <td class="rowdef">
            <%= k++ %>
        </td>
        <td class="rowdef">
            <%= rule.getName() %>
        </td>
        <td class="rowdef">
            <%= rule.getOnEvent() %>
        </td>
        <td class="rowdef">
            <%= rule.getMode() %>
        </td>
        <td class="rowdef">
            <%= ruleSet.getTargetService() %>
        </td>
        <td class="rowdef">
            <%= rule.getPriority() %>
        </td>
        <td valign="top">


            <!-- Parameters Table -->
            <table height="100%" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="titrecon">Name</td>
                    <td class="titrecon">Value</td>
                </tr>
                <%
                    for (int z = 0; z < rule.getParameterCount(); z++) {
                        Parameter p = rule.getParameter(z);
                %>
                <tr>
                    <td class="rowdef">
                        <%= p.getName() %>
                    </td>
                    <td class="rowdef">
                        <%= p.getValue() %>
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
        }
    %>


</table>
<%
} catch (Exception e) {
%>
<p align="center">Error Message:<font color="red">&nbsp;<%=e.getMessage()%>
</font></p>
<%
    }
%>
