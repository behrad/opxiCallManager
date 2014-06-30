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
<%@ page import="com.basamadco.opxi.callmanager.pool.AgentPool" %>
<%@ page import="com.basamadco.opxi.callmanager.pool.WorkgroupAgentPool" %>
<%@ page import="com.basamadco.opxi.callmanager.rule.WorkgroupPresencePlan" %>
<%@ page import="com.basamadco.opxi.callmanager.rule.AbstractRule" %>
<%@ page import="com.basamadco.opxi.callmanager.rule.RuleUsage" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.profile.RuleSet" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.profile.Rule" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.profile.Parameter" %>
<%@ page import="com.basamadco.opxi.callmanager.pool.SkillBasedPool" %>
<% try { %>


<%
    ServiceFactory sf = ((ServiceFactory) application.getAttribute( "com.basamadco.opxi.callmanager.ServiceFactory" ));
    String poolId = request.getParameter( "id" );
    if ( poolId != null ) {
        AgentPool pool = sf.getPoolService().getPool( poolId );
        if ( pool instanceof WorkgroupAgentPool ) {

            WorkgroupAgentPool wg = (WorkgroupAgentPool) pool;

%>
<p>Workgroup Information For <b><%=wg.getName()%>
</b></p>
<table class="mainCon" cellpadding="0" cellspacing="0">
    <tr>

        <td class="titrecon">Start</td>
        <td class="titrecon">End</td>
        <td class="titrecon">Repeat</td>

        <!--<td class="titrecon">pool type</td>-->
    </tr>

    <tr>
        <td class="rowdef"><%=wg.getFrom().getTime()%>
        </td>
        <td class="rowdef"><%=wg.getTo().getTime()%>
        </td>
        <td class="rowdef"><%=wg.getRepeat()%>
        </td>
    </tr>
</table>


<%
    int count = 1;

    Iterator it = wg.getRules().iterator();
    while ( it.hasNext() ) {
        AbstractRule rule = (AbstractRule) it.next();
        try {


%>
<hr/>
<br/>


<table class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td class="titrecon">
            <%= rule.getRuleInfo() %> Usages:
        </td>
    </tr>

    <%
        Iterator usages = rule.getEventContexts().iterator();
        while ( usages.hasNext() ) {
            RuleUsage ruleUsage = (RuleUsage) usages.next();
    %>
    <tr>
        <td class="rowdef">
            <%= ruleUsage.toString() %>
        </td>
    </tr>
    <%
        }
    %>

</table>


<%
        } catch ( Exception e ) {

        }
    }
%>

<%
    }

%>
<hr/>
<br/>
<table align="center" class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td colspan="7" class="titrecon">Profile Rule Set</td>
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
        for ( int i = 0; i < pool.getProfile().getRuleSetCount(); i++ ) {
            RuleSet ruleSet = pool.getProfile().getRuleSet( i );
            for ( int j = 0; j < ruleSet.getRuleCount(); j++ ) {
                Rule rule = ruleSet.getRule( j );

    %>
    <tr>
        <td class="rowdef">
            <%= k++ %>
        </td>
        <td class="rowdef">
            <%= OpxiToolBox.unqualifiedClassName( Class.forName( rule.getName() ) ) %>
        </td>
        <td class="rowdef">
            <%= OpxiToolBox.unqualifiedClassName( Class.forName( rule.getOnEvent() ) ) %>
        </td>
        <td class="rowdef">
            <%= rule.getMode() %>
        </td>
        <td class="rowdef">
            <%= OpxiToolBox.unqualifiedClassName( Class.forName( ruleSet.getTargetService() ) ) %>
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
                    for ( int z = 0; z < rule.getParameterCount(); z++ ) {
                        Parameter p = rule.getParameter( z );
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
} else {
%>
<table class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td class="titrecon">Name</td>
        <td class="titrecon">Type</td>
        <td class="titrecon">Online Members</td>
    </tr>
        <%
        Iterator pools = sf.getPoolService().pools().iterator();
        while ( pools.hasNext() ) {
            AgentPool pool = (AgentPool) pools.next();

            %>
    <tr>
        <td class="rowdef"><%=pool.getName()%>
        </td>
        <td class="rowdef">
            <%
                if ( pool instanceof WorkgroupAgentPool ) {
                    if ( ((WorkgroupAgentPool) pool).getFrom() != null ) {
            %>
            <a href="pools.jsp?id=<%=pool.getName()%>">Work Shift & Rules</a>
            <%
            } else {
            %>
            Support Group for Skill
            <%
                }
            } else if ( pool instanceof SkillBasedPool ) {
            %>
            <a href="queue2.jsp">Queue Target</a>
            <%
                }

            %>
        </td>
        <td class="rowdef"><%=pool.agentViewStr()%>
        </td>
    </tr>
        <%



        }
%>

        <%
    }
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