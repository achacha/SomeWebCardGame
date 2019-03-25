<%@ page import="org.achacha.base.context.CallContext" %>
<%@ page import="org.achacha.base.context.CallContextTls" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Where to now?</title>
    <style>
        a {
            font-weight: bold;
            font-size: 24px;
        }
    </style>
</head>
<body>
<%
    if (CallContextTls.get().getLogin() == null) {
        session.setAttribute(CallContext.SESSION_REDIRECT_FROM, "/");
        response.sendRedirect("/login.jsp");
        return;
    }
%>

<div>
<a href="index-phaser.jsp">Phaser</a><br/>
<a href="./dist/vue-app.html">Vue</a>
</div>

</body>
</html>