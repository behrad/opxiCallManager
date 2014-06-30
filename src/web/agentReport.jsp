<%
    String msg = (String) request.getAttribute( "msg" );
    if ( msg != null ) {
%>
<p class="msg_green"><%= msg %>
</p>
<%
    }
%>