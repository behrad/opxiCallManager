<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.basamadco.opxi.callmanager.util.OpxiToolBox"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallService"%>
<%@ page import="com.basamadco.opxi.callmanager.call.CallServiceFactory"%>
<%@ page import="com.basamadco.opxi.callmanager.call.OutboundCall" %>
<%@ page import="javax.servlet.sip.SipURI" %>
<% try { %>
    <table width="90%" cellspacing="0" class="mainCon">
			<tr>
				<td class="titrecon">#</td>
				<td class="titrecon">Call Id</td>
				<td class="titrecon">Caller</td>
				<td class="titrecon">Status</td>
				<td class="titrecon">Target</td>
				<td class="titrecon">Duration</td>
                <td class="titrecon">Actions</td>
                <!--<td class="titrecon">Handler Agent</td>-->
			</tr>
        <%
            Collection list = com.basamadco.opxi.callmanager.call.CallServiceFactory.callList();
            if (list.size() == 0) {
        %>
        		</table>
				<p class="msg">No call in service</p>
		<%
        	} else {
                int count = 1;
                for (Iterator i = list.iterator(); i.hasNext(); count++ ) {
                    CallService call = (CallService)i.next();
                    if( call instanceof OutboundCall ) {
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
					<td class="row<%=color%>"><a href="<%=request.getContextPath()%>/showCall.jsp?id=<%=call.getId()%>"><%=call.getId()%></a></td>
					<td class="row<%=color%>"><%=call.getCallerAddress().getURI()%></td>
					<td class="row<%=color%>"><%=call.getStateString()%></td>
                    <td class="row<%=color%>"><%=call.getTarget() != null ? call.getTarget().getName() : ( (SipURI) call.getTargetAddress().getURI() ).getUser() %></td>
                    <td class="row<%=color%>"><%=OpxiToolBox.duration( call.duration() )%></td>
                    <%--<td class="row<%=color%>" align="center"><%= (call.getHandlerAgent()==null)?"---":call.getHandlerAgent()%></td>--%>
                    <td class="row<%=color%>">
                        <a href="<%=request.getContextPath()%>/transfer.jsp?callId=<%=call.getId()%>">transfer</a>
                        ,
                        <a href="<%=request.getContextPath()%>/teardown.jsp?callId=<%=call.getId()%>">teardown</a>
                    </td>
                </tr>
        <%
            	    }
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
       