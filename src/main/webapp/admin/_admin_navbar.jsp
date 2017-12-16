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
<div>
<nav class="nav navbar-light bg-faded">
    <a class="navbar-brand" href="./index.jsp"><img width="42" height="32" src="/static/images/fjord_logo.jpg"/></a>
    <ul class="nav">
        <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">Info</a>
            <div class="dropdown-menu">
                <a class="dropdown-item" href="serverStatus.jsp">Server Status</a>
                <a class="dropdown-item" href="dbStatus.jsp">Database Status</a>
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
    <span>
        <b class="navbar-brand">&nbsp; &#183; &nbsp; <span id="admin_subhead"></span></b>
    </span>
    <span class="navbar-text">
        <small style='float:right'>Host: <b><%=Global.getHOSTNAME()%></b> Time: <b><%=LocalDateTime.now()%></b> Username: <b><%=login.getEmail()%></b><br/>
            <a href="..">Goto App Home</a>&nbsp;&nbsp;
            <a href="#" onclick="doRestCallWithReload('DELETE','../v1/Login')">Logout</a>
        </small>
    </span>
</nav>
</div>
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
<%--Placeholder for popup dialog--%>
<div id="rest_call_result" class="modal fade"
     tabindex="-1" role="dialog"
     aria-labelledby="rest_call_result_label" aria-hidden="true"></div>
