<%@ page import="org.achacha.base.dbo.EventLogDbo" %>
<%@ page import="org.achacha.base.dbo.EventLogDboFactory" %>
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="en">
<head>
    <jsp:include page="_admin_header.jsp"/>
</head>
<body>
<jsp:include page="_admin_navbar.jsp"/>
<script>setNavbarHeader('Event Log: Last 250')</script>

<table class="table table-sm table-bordered table-hover table-striped" style="line-height:1.0;width:100%">
    <tr>
        <th>id</th>
        <th>createdOn</th>
        <th>event</th>
        <th>loginId</th>
        <th>data</th>
    </tr>

    <%
        for (EventLogDbo e : EventLogDboFactory.loadLast250()) {
    %>
    <tr>
        <td><small><%=e.getId()%></small></td>
        <td><small><%=e.getCreatedOn()%></small></td>
        <td><small><%=e.getEvent()%></small></td>
        <td><small><%=e.getLoginId()%></small></td>
        <td><small><%=e.getData()%></small></td>
    </tr>
    <%
        }
    %>
</table>

</body>
<jsp:include page="_admin_footer.jsp"/>
</html>
