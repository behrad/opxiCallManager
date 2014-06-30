<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.basamadco.opxi.callmanager.queue.*" %>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox" %>
<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory" %>
<% try { %>
<%
    //Collection messages = (Collection)request.getAttribute( "queueList" );
    ServiceFactory sf = ((ServiceFactory) application.getAttribute("com.basamadco.opxi.callmanager.ServiceFactory"));
    Collection messages = sf.getQueueManagementService().queueList();
%>
<table align="center" class="mainCon" cellpadding="0" cellspacing="0">
    <tr>
        <td class="titrecon">#</td>
        <td class="titrecon">Tel#</td>
        <td class="titrecon">Name</td>
        <td class="titrecon">Depth</td>
        <td class="titrecon">Calls In Handle</td>
        <td class="titrecon">Calls Pending</td>
        <td class="titrecon">Wait Time Limit</td>
        <td class="titrecon">Schedule Idle Time</td>
        <td class="titrecon">Wait Time</td>
        <td class="titrecon">Rule Usages</td>
        <td class="titrecon">Profile</td>

        <!--<td class="titrecon">waiting media</td>-->
    </tr>
    <%
        int count = 1;
        for (Iterator i = messages.iterator(); i.hasNext(); count++) {
            Queue queue = (Queue) i.next();
    %>
    <tr>
        <td class="rowdef" align="center"><%=count%>
        </td>
        <td class="rowdef" align="center"><%= queue.getTelephoneNumber() %>
        </td>
        <td class="rowdef" align="center">
            <a href="<%=request.getContextPath()%>/show.jsp?id=<%=queue.getName()%>"><%=queue.getName()%>
            </a>
        </td>
        <td class="rowdef" align="center"><%=queue.getQueueDepth()%>
        </td>
        <%--<td class="rowdef" align="center"><%=queue.getCallAttempts()%></td>--%>
        <td class="rowdef" align="center"><%=queue.getInHandleCallsSize()%>
        </td>
        <td class="rowdef" align="center"><%=queue.getPendingCallsSize()%>
        </td>
        <td class="rowdef" align="center"><%=queue.getMaxCallWaitTime() / 1000 + "s"%>
        </td>
        <td class="rowdef" align="center"><%=queue.getIdleTimeToSchedule() + "s"%>
        </td>
        <!--<td class="rowdef" align="center"></td>-->
        <td class="rowdef" align="center"><%=OpxiToolBox.duration(queue.longestWaitTime())%>
        </td>
        <td class="rowdef" align="center">
            <a href="<%=request.getContextPath()%>/ruleSetInfo.jsp?id=<%=queue.getName()%>">view</a>
        </td>
        <td class="rowdef" align="center">
            <a href="<%=request.getContextPath()%>/queueInfo.jsp?id=<%=queue.getName()%>">view</a>
        </td>
        <%--<td class="rowdef" align="center"><a href="<%=queue.getTarget().getWaitingMsgURI()%>"><%=queue.getTarget().getWaitingMsgURI()%></a></td>--%>
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