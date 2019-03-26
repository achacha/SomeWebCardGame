<%@ page import="org.achacha.base.context.CallContext" %>
<%@ page import="org.achacha.base.context.CallContextTls" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (CallContextTls.get().getLogin() == null) {
        session.setAttribute(CallContext.SESSION_REDIRECT_FROM, "/");
        response.sendRedirect("/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title></title>
    <script src="./js/vue/dist/vue.js"></script>
    <script src="./js/axios/dist/axios.js"></script>
</head>
<body>

<div id="app">
    <span v-bind:title="message">Hover over to see title!</span><br/>
    <div style="background-color: floralwhite"><login-view></login-view></div>
</div>

</body>
<script type="text/javascript" src="app/login-view.js"></script>
<script type="text/javascript" src="app/app.js"></script>
</html>