<%@ page import="org.achacha.base.context.CallContext" %>
<%@ page import="org.achacha.base.context.CallContextTls" %>
<%@ page import="org.achacha.webcardgame.helper.ResponseHelper" %>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="en">
<head>
    <jsp:include page="_admin_header.jsp"/>
</head>
<body>
<jsp:include page="_admin_navbar.jsp"/>
<script>setNavbarHeader('Debug Request')</script>

<%
    CallContext ctx = CallContextTls.get();
    out.println(ResponseHelper.toHtml(ctx));
%>

</body>
<jsp:include page="_admin_footer.jsp"/>
</html>
