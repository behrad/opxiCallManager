<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallService"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallServiceFactory"%>
<%@ page import="com.basamadco.opxi.callmanager.call.OutboundCall" %>
<%@ page import="javax.servlet.sip.SipURI" %>
<%@ page import="com.basamadco.opxi.callmanager.util.LockManager" %>
<%@ page import="com.basamadco.opxi.callmanager.sip.listener.SipSessionManager" %>
<% try { %>
<p class="title">Available SipSession objects in call manager session manager</p>

    <table width="90%" cellspacing="0" class="mainCon">
			<tr>
				<td class="titrecon">#</td>
				<td class="titrecon">Session CallId</td>
			</tr>
        <%
            Collection list = SipSessionManager.listSessions();
            if (list.size() == 0) {
        %>
        		</table>
				<p class="msg">No sipSession exists</p>
		<%
        	} else {
                int count = 1;
                for (Iterator i = list.iterator(); i.hasNext(); count++ ) {
                    Object sipSession = (Object)i.next();
                        String color = "def";
                        if( count % 2 != 0 ) {
			                color = "swing";
                        }
//						switch( call.getState() ) {
//							case CallService.QUEUED:
//								color = "gray";
//								break;
//							case CallService.TRANSFERING:
//								color = "gray";
//								break;
//							case CallService.MAKE_CALL:
//							case CallService.ASSIGNED:
//							case CallService.IN_GREETING:
//								color = "swing";
//								break;
//                            case CallService.RINGING:
//                                color = "yellow";
//                                break;
//                            case CallService.WAITING:
//								color = "blue";
//								break;
//						}
        %>

				<tr>
					<td class="row<%=color%>"><%=count%></td>
					<td class="row<%=color%>"><%=sipSession%></td>
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