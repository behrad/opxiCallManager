<%@ page import="java.util.List"%>
<%@ page import="com.basamadco.opxi.callmanager.logging.LogValueObject"%>

<html>
<body>
<h2 align="center"><font color="#000066">Opxi Call Manager Server State Log</font></h2>
	<table width="100%" align="center">
	<tr>
	<td valign="top">
        <ul>
        <%
            List data = (List)request.getAttribute( "logs" );
            for (int i = 0; i < data.size(); i++) {
                LogValueObject logValueObject = (com.basamadco.opxi.callmanager.logging.LogValueObject) data.get( i );

        %>
                <li>
                    <A HREF="<%=request.getContextPath()%>/billing?logVOId=<%=logValueObject.getId()%>"><%= logValueObject.getId() %></A>
                    </br>
                    </br>
                </li>
        <%

            }
        %>
        </ul>
    </td>
    </tr>
	</table>



</body>
</html>