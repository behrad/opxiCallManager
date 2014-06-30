<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="../template/taglibs.jsp"%>

<html>
<html:base/>
<head>
	<META HTTP-EQUIV="refresh" CONTENT="10">
	<META HTTP-EQUIV="expires" CONTENT="-1">
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
	<META HTTP-EQUIV="CacheControl" CONTENT="no-cache">
    <title><tiles:getAsString name="title"/></title>
    <link href="<%=request.getContextPath()%>/template/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <table width="100%" align="center" class="mainCon" cellpadding="0" cellspacing="0" height="90%">
        <tr valign="top" height="5%">
            <td>
                <h2 align="center" class="title">
                    <tiles:getAsString name="headerMessage"/>
                </h2>
            </td>
		</tr>
		<tr valign="top" align="center">
            <td align="center">
				<tiles:insert name="body"/>
            </td>
        </tr>
    </table>
    <hr width="50%">
	<p class="text">
		Copyright © 2006 AC&C Basamad Co.
	</p>
</body>
</html>