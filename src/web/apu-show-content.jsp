<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory" %>
<%@ page import="com.basamadco.opxi.callmanager.entity.UserAgent" %>
<%@ page import="com.basamadco.opxi.callmanager.sip.presence.PublishContext" %>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox" %>
<% try {
    String aor = request.getParameter("ua");
%>

<p>Presence Rule Usage Information for: <%= aor %>
</p>

<%
    //Collection messages = (Collection)request.getAttribute( "queueList" );
    ServiceFactory sf = ((ServiceFactory) application.getAttribute("com.basamadco.opxi.callmanager.ServiceFactory"));
    PublishContext ctx = sf.getPresenceService().getActivePresenceContext(new UserAgent(aor));
%>
<table class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td class="titrecon">CtxId</td>
        <td class="rowdef"><%= ctx.getPublishRequest().getCallId()%>
        </td>
    </tr>
    <tr>
        <td class="titrecon">Total Used Time</td>
        <td class="rowdef"><%= OpxiToolBox.duration(ctx.getUsageCtx().getTotalUsageTime())%>
        </td>
    </tr>
    <tr>
        <td class="titrecon">Total Usages Count</td>
        <td class="rowdef"><%= ctx.getUsageCtx().getUsageCount() %>
        </td>
    </tr>
    <tr>
        <td class="titrecon">Last Comment</td>
        <td class="rowdef"><%= ctx.getUsageCtx().getComment() %>
        </td>
    </tr>
    <tr>
        <td class="titrecon">Current Usage Duration</td>
        <td class="rowdef"><%= OpxiToolBox.duration(ctx.getUsageCtx().getCurrentInUseTime()) %>
        </td>
    </tr>
    <tr>
        <td class="titrecon">Context Information</td>
        <td class="rowdef"><%= ctx.toString() %>
        </td>
    </tr>
</table>

<%
} catch (Exception e) {
%>
<div align="center">Error Message:<p class="meg">&nbsp;<%=e %>
    <%
        e.printStackTrace();
    %>
</p></div>
<%
    }
%>