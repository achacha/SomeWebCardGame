<%@ page import="org.achacha.base.context.CallContext" %>
<%@ page import="org.achacha.base.context.CallContextTls" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (CallContextTls.get().getLogin() == null) {
        session.setAttribute(CallContext.SESSION_REDIRECT_FROM, "/index-vue.jsp");
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
    <script src="./js/phaser-ce/build/phaser.js"></script>
    <script src="./js/axios/dist/axios.js"></script>
</head>
<body>

<div id="app">
    <div style="background-color: floralwhite"><login-view></login-view></div>
    <div style="background-color: darksalmon"><player-view></player-view></div>
    <div style="background-color: darksalmon"><phaser-view></phaser-view></div>
    <!-- TODO: Add component of active adventures -->
    <!-- TODO: Add component of available adventures -->
</div>

</body>
<script type="text/javascript" src="app/login-view.js"></script>
<script type="text/javascript" src="app/player-view.js"></script>
<script type="text/javascript" src="app/phaser-view.js"></script>
<script type="text/javascript" src="app/app.js"></script>
</html>