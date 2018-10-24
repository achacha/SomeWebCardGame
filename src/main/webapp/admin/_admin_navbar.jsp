<%@ page import="org.achacha.base.context.CallContext" %>
<%@ page import="org.achacha.base.dbo.LoginUserDbo" %>
<%@ page import="org.achacha.base.global.Global" %>
<%@ page import="java.time.LocalDateTime" %>
<%
    LoginUserDbo login = CallContext.fromSession(request.getSession());
    if (null == login) {
        out.println("<big>ERROR!  Not logged in and Tomcat filter not configured, admin disabled.</big>");
    }
    else {
        if (login.isSuperuser()) {
%>
<script type="text/javascript">
    function doLogout() {
        axios.delete(
            '/api/auth/login'
        ).then(function (response) {
            console.log("data=" + response.data);
            window.location.reload(true);
        }).catch(function(error) {
            console.log("error=" + error);
            window.location.reload(true);
        });
        return false;
    }
</script>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="./index.jsp"><img width="42" height="32" src="../static/images/fjord_logo.jpg"/></a>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">Info</a>
                <div class="dropdown-menu">
                    <a class="dropdown-item" href="serverStatus.jsp">Server Status</a>
                    <a class="dropdown-item" href="dbStatus.jsp">Database Status</a>
                    <a class="dropdown-item" href="callContextDump.jsp">CallContext Dump</a>
                    <a class="dropdown-item" href="callContextLog.jsp">CallContext Log</a>
                    <a class="dropdown-item" href="eventlogTodayLast250.jsp">Event Log - Last 250 - 1 day range</a>
                    <a class="dropdown-item" href="eventlogAnyLast250.jsp">Event Log - Last 250</a>
                    <a class="dropdown-item" href="dboViewer.jsp">Dbo Viewer</a>
                    <%--<div class="dropdown-divider"></div>--%>
                    <%--<a class="dropdown-item" href="#">Separated link</a>--%>
                </div>
            </li>
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">Settings</a>
                <div class="dropdown-menu">
                    <a class="dropdown-item" href="log4j2Admin.jsp">Log4j2 Settings</a>
                </div>
            </li>
            <%--<li class="nav-item">--%>
            <%--<a class="nav-link disabled" href="#">Disabled</a>--%>
            <%--</li>--%>
        </ul>
    </div>
    <span class='navbar-text'>&nbsp;&nbsp;</span>
    <span class="navbar-text mr-auto" id="admin_subhead"></span>
    <span class='navbar-text'>&nbsp;&nbsp;&nbsp;</span>
    <span class='navbar-text' style='float:right; font-size: xx-small'>
        Host: <b><%=Global.getHOSTNAME()%></b>
            <br/>Time: <b><%=LocalDateTime.now()%></b>
            <br/>Username: <b><%=login.getEmail()%></b><br/>
            <a href="..">Goto App Home</a>&nbsp;&nbsp;
            <a href="#" onclick="doLogout()">Logout</a>
    </span>
</nav>

<%--Placeholder for popup dialog--%>
<div id="rest_call_result"
     style="display:none"
></div>

<script src="js/_admin_navbar.js"></script>
<%
        } // su
        else {
            session.removeAttribute(CallContext.SESSION_REDIRECT_FROM);
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", "/");
            return;
        }
    } // login null check
%>
