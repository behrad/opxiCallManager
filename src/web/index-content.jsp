<p>Opxi Call Manager - Web Console</p>
<br>
<%
    String appServerName = application.getServerInfo();
    boolean isWSAS = false;
    if ( appServerName.indexOf( "IBM" ) > -1 ) {
        isWSAS = true;
    }
%>

<p>
    <span class="msg"><!--blink>This deployment is a testing, unstable and transient version</blink--></span>
</p>
<br>

<p><b>Deployment Name:</b> <%= application.getServletContextName() %>
</p>

<p><b>Deployment Version:</b> Release Candidate 1.3 - Based on 3/2008 version </p>

<p><b>Hosted On:</b> <%=application.getServerInfo() %>
</p>
</br>
</br>
</br>
</br>
</br>
<p>
    <span class="msg">Providing Flexible Skill-Based Enterprise Queues and Connecting Networks</span>
</p>
<%--<p><span  class="msg">Latest fixes:</span>--%>
<%--<ul>--%>
<%--<li>--%>
<%--Add "trunkDefaultAccess" attribute to Agent profile to control outgoing calls access by agents(5/29/08)--%>
<%--</li>--%>
<%--<li>--%>
<%--Fixed a bug when reading class list from file system in Windows with paths with white spaces(5/27/08)--%>
<%--</li>--%>
<%--<li>--%>
<%--Postpone removing Leg references from sipSessions after first appSession timeout when leg is in IDLE state(5/27/08)--%>
<%--</li>--%>
<%--<li>--%>
<%--Terminate all active calls with connect time more that "alive call duration".(5/22/08)--%>
<%--</li>--%>
<%--<li>--%>
<%--Cleaned some NoLegAttributeExceptions, but not all yet. (some logs needed to be checked)(5/22/08)--%>
<%--</li>--%>
<%--<li>--%>
<%--Refactored registrar service--%>
<%--</li>--%>
<%--<li>--%>
<%--Refactored presence and agent services--%>
<%--</li>--%>
<%--<li>--%>
<%--Added tranferred log item for transfer targets--%>
<%--</li>--%>
<%--<li>--%>
<%--Fixed a bug when writing closed loggs on webdav server--%>
<%--</li>--%>
<%--<li>--%>
<%--Fixed a number of little bugs (call assignment to agents, send admin IMs, ...) --%>
<%--</li>--%>
<%--<li>--%>
<%--Fixed subscription bug when notifier has no presence context--%>
<%--</li>--%>
<%--<li>--%>
<%--Add validity check for subscription request notifiers--%>
<%--</li>--%>
<%--</ul>--%>
<%--</p>--%>