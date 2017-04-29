<!DOCTYPE html>
<%@ page import="org.achacha.base.db.DatabaseHelper" %>
<%@ page import="org.achacha.base.global.Global" %>
<%@ page import="org.achacha.base.json.JsonHelper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <jsp:include page="_admin_header.jsp"/>
</head>
<body>
<jsp:include page="_admin_navbar.jsp"/>
<script>$('#admin_subhead').text('Database Status')</script>
<table class="table table-sm table-bordered table-hover table-striped" style="width:100%; line-height:1.0;">
    <tbody>
    <tr>
        <th>PostgreSQL DS</th>
        <td>
            <pre><%= JsonHelper.toStringPrettyPrint(
                        DatabaseHelper.toJsonObject(
                            Global.getInstance().getDatabaseManager().getDatabaseConnectionProvider().getDataSource())) %></pre>
        </td>
    </tr>
    </tbody>
</table>
<br/>
<%
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>version</h2><br/>");
        DatabaseHelper.toHtmlTable(
            "select version()",
            sb);

        sb.append("<br/><h2>pg_stat_database</h2><br/>");
        DatabaseHelper.toHtmlTable(
            "select * from pg_stat_database"
            , sb);

        sb.append("<br/><h2>pg_stat_user_tables</h2><br/>");
        DatabaseHelper.toHtmlTable(
                "select * from pg_stat_user_tables"
                , sb);

        sb.append("<br/><h2>pg_statio_user_tables</h2><br/>");
        DatabaseHelper.toHtmlTable(
                "select * from pg_statio_user_tables"
                , sb);

        sb.append("<br/><h2>pg_stat_user_indexes</h2><br/>");
        DatabaseHelper.toHtmlTable(
                "select * from pg_stat_user_indexes"
                , sb);

        sb.append("<br/><h2>pg_statio_user_indexes</h2><br/>");
        DatabaseHelper.toHtmlTable(
                "select * from pg_statio_user_indexes"
                , sb);

        sb.append("<br/><h2>pg_statio_user_sequences</h2><br/>");
        DatabaseHelper.toHtmlTable(
                "select * from pg_statio_user_sequences"
                , sb);

        sb.append("<br/><h2>pg_stat_user_functions</h2><br/>");
        DatabaseHelper.toHtmlTable(
                "select * from pg_stat_user_functions"
                , sb);
        out.write(sb.toString());
%>

</body>
<jsp:include page="_admin_footer.jsp"/>
</html>
