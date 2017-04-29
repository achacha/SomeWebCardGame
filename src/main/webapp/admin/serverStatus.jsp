<!DOCTYPE html>
<%@ page import="org.achacha.base.global.Global" %>
<%@ page import="org.achacha.base.json.JsonHelper" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <jsp:include page="_admin_header.jsp"/>
</head>
<body>
<jsp:include page="_admin_navbar.jsp"/>
<script>$('#admin_subhead').text('Server Status')</script>
<table class="table table-sm table-bordered table-hover table-striped" style="width:100%; line-height:1.0;">
    <tbody>
    <tr>
        <th>Application Name</th>
        <td><%=Global.getInstance().getApplication() %>
        </td>
    </tr>
    <tr>
        <th>Deployment mode</th>
        <td><%=Global.getInstance().getMode() %>
        </td>
    </tr>
    <tr>
        <th>Global Properties</th>
        <td>
            <pre><%=JsonHelper.toStringPrettyPrint(Global.getInstance().getProperties().toJsonObject()) %></pre>
        </td>
    </tr>
    <tr>
        <th>Java Environment</th>
        <td>
<pre>java.version=<%= System.getProperty("java.version") %>
java.class.path=<%= System.getProperty("java.class.path") %>
java.home=<%= System.getProperty("java.home") %>
os.arch=<%= System.getProperty("os.arch") %>
os.name=<%= System.getProperty("os.name") %>
os.version=<%= System.getProperty("os.version") %></pre>
        </td>
    </tr>
    <tr>
        <th>Server Environment</th>
        <td>
servletContext.serverInfo=<%= config.getServletContext().getServerInfo() %><br/>
servletContext.virtualServerName=<%= config.getServletContext().getVirtualServerName() %><br/>
servletContext.version=<%= config.getServletContext().getMajorVersion() %>.<%= config.getServletContext().getMinorVersion() %><br/>
servletContext.contextPath=<%= config.getServletContext().getContextPath() %><br/>
servletContext.filterRegistrations=
<table class="table">
    <tbody>
<%
    for (Map.Entry<String, ? extends FilterRegistration> entry : config.getServletContext().getFilterRegistrations().entrySet()) {
        out.println("<tr><th>"+entry.getKey()+"</th><td>"+entry.getValue().getClassName()+"</td></tr>");
    }
%>
    </tbody>
</table><br/>

servletContext.servletRegistrations=
<table class="table">
    <tbody>
<%
    for (Map.Entry<String, ? extends ServletRegistration> entry : config.getServletContext().getServletRegistrations().entrySet()) {
        out.println("<tr><th>"+entry.getKey()+"</th><td>"+entry.getValue().getClassName()+"</td></tr>");
    }
%>
    </tbody>
</table><br/>

servletContext.servletContextName=<%= config.getServletContext().getServletContextName() %><br/>
servletContext.jspConfigDescriptor=<%= config.getServletContext().getJspConfigDescriptor() %><br/>

servletContext.initParameters={
<table class="table">
    <tbody>
<%
    Enumeration<String> einit = config.getInitParameterNames();
    while (einit.hasMoreElements()) {
        String name = einit.nextElement();
        out.println("<tr><th>"+name+"</th><td>"+config.getInitParameter(name)+"</td></tr>");
    }
%>
    </tbody>
</table><br/>

servletContext.attributes={
<table class="table">
    <tbody>
<%
    Enumeration<String> eattr = config.getServletContext().getAttributeNames();
    while (eattr.hasMoreElements()) {
        String name = eattr.nextElement();
        out.println("<tr><th>"+name+"</th><td>"+config.getServletContext().getAttribute(name)+"</td></tr>");

    }
%>
    </tbody>
</table><br/>
}
        </td>
    </tr>
    </tbody>
</table>
</body>
<jsp:include page="_admin_footer.jsp"/>
</html>
