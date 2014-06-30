<%@ page import="com.basamadco.opxi.callmanager.ServiceFactory"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.util.List"%>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox" %>
<% try { %>
<p class="title">Loaded services in call manager</p>

    <table width="90%"  cellspacing="0" class="mainCon">
			<tr>
				<td class="titrecon">#</td>
				<td class="titrecon">Service Name</td>
			</tr>
        <%
            ServiceFactory sf = ((ServiceFactory)application.getAttribute( "com.basamadco.opxi.callmanager.ServiceFactory" ) );
            List services = sf.listServices();
            if (services.size() == 0) {
        %>
        		</table>
				<p class="msg">No services!!!</p>
		<%
        	} else {
                int count = 1;
                for (Iterator i = services.iterator(); i.hasNext(); count++ ) {
                    Object service = (Object)i.next();
                        String color = "def";
                        if( count % 2 != 0 ) {
			                color = "swing";
                        }
        %>

				<tr>
					<td class="row<%=color%>"><%=count%></td>
                    <td class="row<%=color%>">
                        <a href=<%="serviceMemory.jsp?type="+service.getClass().getName()%>>
                            <%=OpxiToolBox.unqualifiedClassName( service.getClass() )%>
                        </a>
                    </td>
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