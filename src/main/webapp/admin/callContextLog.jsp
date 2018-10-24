<%@ page import="org.achacha.base.context.CallContextLog" %>
<%@ page import="org.achacha.base.context.CallContextTls" %>
<%@ page import="java.time.Instant" %>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="en">
<head>
    <jsp:include page="_admin_header.jsp"/>
</head>
<body>
<jsp:include page="_admin_navbar.jsp"/>
<script>setNavbarHeader('Context Log')</script>

<h2>Event Log</h2>
<table class="table table-sm table-bordered table-hover table-striped" style="width:100%; line-height:1.0;">
    <thead>
    <tr>
        <th>Created</th>
        <th>URI</th>
        <th>Duration (ms)</th>
    </tr>
    </thead>
    <tbody>
    <%
        CallContextLog contextLog = CallContextTls.getContextLog();
        for (CallContextLog.Event e : contextLog.getEventsLog()) {
            out.print("<tr><td>");
            out.print(Instant.ofEpochMilli(e.getCreatedTimeMillis()).toString());
            out.print("</td><td>" + e.getUri() + "</td><td>" + e.getDurationInMillis() + "</td></tr>\n");
        }
    %>
    </tbody>
</table>

<br/>
<hr/>
<br/>

<h2>Slow Log (Threshhold <%=contextLog.getSlowThresholdInMillis()%>ms)</h2>
<table class="table table-condensed table-bordered table-striped">
    <thead>
    <tr>
        <th>Created</th>
        <th>URI</th>
        <th>Duration (ms)</th>
    </tr>
    </thead>
    <tbody>
    <%
        for (CallContextLog.Event e : contextLog.getEventsSlow()) {
            out.print("<tr><td>");
            out.print(Instant.ofEpochMilli(e.getCreatedTimeMillis()).toString());
            out.print("</td><td>" + e.getUri() + "</td><td>" + e.getDurationInMillis() + "</td></tr>\n");
        }
    %>
    </tbody>
</table>


</body>
<jsp:include page="_admin_footer.jsp"/>
</html>
