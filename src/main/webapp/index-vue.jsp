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
    <%@include file="_header.jsp"%>
    <script src="./js/vue/dist/vue.js"></script>
    <script src="./js/phaser-ce/build/phaser.js"></script>
    <script src="./js/axios/dist/axios.js"></script>
</head>
<body style="background-color: gray">

<div id="app" class="container-fluid">
    <div class="row">
        <div class="col-12">
            <div style="background-color: dimgrey"><login-view></login-view></div>
        </div>
    </div>
    <div class="row">
        <div class="col-xl">
            <div style="background-color: darksalmon"><phaser-view></phaser-view></div>
        </div>
        <div class="col-sm">
            <div style="background-color: darksalmon"><player-view></player-view></div>
        </div>
        <!-- TODO: Add component of active adventures -->
        <!-- TODO: Add component of available adventures -->
    </div>
</div>

</body>
<script type="text/javascript" src="app/login-view.js"></script>
<script type="text/javascript" src="app/player-view.js"></script>
<script type="text/javascript" src="app/phaser-view.js"></script>
<script type="text/javascript" src="app/app.js"></script>
<script type="text/javascript" src="app/phaser-app.js"></script>
</html>