<table align="center">
    <tr>
        <td valign="top">
            <ul>
                <%
                    String appServerName = application.getServerInfo();
                    boolean isWSAS = false;
                    if( appServerName.indexOf( "IBM") > -1 ) {
                        isWSAS = true;
                    }
                %>
                <%
                    if( isWSAS ) {
                %>
                <li><a href="agents.jsp">Agent Monitor</a></li><br>
                <li><a href="queue2.jsp">Queue Monitor</a></li><br>
                <li><a href="application.jsp">Application Monitor</a></li><br>
                <li><a href="log?type=com.basamadco.opxi.callmanager.logging.OpxiActivityLogger">
                    Service Activity Log</a></li><br>
                <li><a href="transfer.jsp">Call Transfer Console</a></li><br>
                <%
                } else {
                %>
                <li><a href="agent.jsp">Agent Monitor</a></li><br>
                <li><a href="queue.jsp">Queue Monitor</a></li><br>
                <%
                    }
                %>
            </ul>
        </td>
    </tr>
    <tr>
        <td class="mainText">
            <b>Deployed Application Name:</b> <%= application.getServletContextName() %>
        </td>
    </tr>
    <tr>
        <td class="mainText">
            <b>Hosted On:</b> <%=application.getServerInfo() %>
        </td>
    </tr>
</table>