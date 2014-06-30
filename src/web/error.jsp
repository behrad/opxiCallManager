<%
    Exception error = (Exception)request.getAttribute( "exception" );
%>
<table>
    <tr><th><font color="red"><%= error.getMessage() %></font></th></tr>
    <TR><td><font color="red"><%= error.getClass() %></font></td></TR>
    <% if( error.getCause() != null ) { %>
        <TR><td><font color="red"><%= error.getCause() %></font></td></TR>
    <% } %>
</table>

