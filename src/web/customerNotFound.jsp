<% response.setCharacterEncoding( "UTF8" ); %>
<html>
<head>
    <title>Opxi Call Manager - Error Page</title>
    <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<table width="100%" align="center" class="mainCon" cellpadding="0" cellspacing="0" height="90%">
    <tr valign="top" align="center">
        <td align="center"><font color="red"><%=request.getAttribute( "msg" )%></font></td>
    </tr>
</table>
<hr width="50%">
<p class="text">
    Copyright © 2006 AC&C Basamad Co.
</p>
</body>
</html>