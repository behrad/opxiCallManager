<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.List" %>
<%
    try {
        String serviceType = request.getParameter( "type" );
%>

    <table width="90%" cellspacing="0" class="mainCon">
			<tr>
				<td class="titrecon">#</td>
				<td class="titrecon">Object Info</td>
			</tr>
        <%
            ServiceFactory sf = ((ServiceFactory)application.getAttribute( "com.basamadco.opxi.callmanager.ServiceFactory" ) );
            List list = sf.getServiceByName( serviceType ).listObjects();
            if (list.size() == 0) {
        %>
        		</table>
				<p class="msg">No service objects exist</p>
		<%
        	} else {
                int count = 1;
                for (Iterator i = list.iterator(); i.hasNext(); count++ ) {
                    Object object = i.next();
                        String color = "def";
                        if( count % 2 != 0 ) {
			                color = "swing";
                        }
        %>

				<tr>
					<td class="row<%=color%>"><%=count%></td>
					<td class="row<%=color%>"><%=object%></td>
                </tr>
        <%
            	}
                %>
            </table>
        <%
            }
        %>
    </p>
    <%
        } catch( Exception e ) {
    %>
            <p align="center">Error Message:<font color="red">&nbsp;<%=e.getMessage()%></font></p>
    <%
        }
    %>       