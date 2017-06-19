<!DOCTYPE html>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="org.apache.logging.log4j.Level" %>
<%@ page import="org.apache.logging.log4j.core.Logger" %>
<%@ page import="org.apache.logging.log4j.core.LoggerContext" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.TreeSet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <jsp:include page="_admin_header.jsp"/>
</head>
<body>
<jsp:include page="_admin_navbar.jsp"/>
<script>$('#admin_subhead').text('Log4j2 Settings')</script>
<%
    final Level[] LOGGER_LEVELS = new Level[] {
            Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL, Level.OFF
    };

    Logger rootLogger = LoggerContext.getContext().getRootLogger();  // Accessing root logger will create it if not already there
    final String loggertToUpate = request.getParameter("logger");
    final String newLevel = request.getParameter("level");
    if (loggertToUpate != null && newLevel != null) {
        if ("ROOT".equals(loggertToUpate))
            LoggerContext.getContext().getRootLogger().setLevel(Level.getLevel(newLevel));
        else
            LoggerContext.getContext().getLogger(loggertToUpate).setLevel(Level.getLevel(newLevel));
    }
%>
<div id="content">
    <h1>Log4J Administration</h1>
    <div>
        <b>Root logger: </b>
<%--<%--%>
    <%--// Render ROOT level logger--%>
    <%--StringBuilder rootHtmlLevels = new StringBuilder();--%>
    <%--for (Level level : LOGGER_LEVELS) {--%>
        <%--if (rootLogger.getLevel() == level) {--%>
            <%--rootHtmlLevels--%>
                    <%--.append("<button class='btn btn-primary btn-sm active'");--%>
        <%--}--%>
        <%--else {--%>
            <%--rootHtmlLevels--%>
                    <%--.append("<a href='")--%>
                    <%--.append(request.getRequestURI())--%>
                    <%--.append("?")--%>
                    <%--.append("logger=ROOT")--%>
                    <%--.append("&level=").append(level.name())--%>
                    <%--.append("'>")--%>
                    <%--.append("<button type='submit' class='btn btn-secondary btn-sm'");--%>
        <%--}--%>

        <%--rootHtmlLevels.append(" value='").append(level.intLevel()).append("'>").append(level.name()).append("</button>");--%>

        <%--if (rootLogger.getLevel() != level) {--%>
            <%--rootHtmlLevels.append("</a>");--%>
        <%--}--%>
    <%--}--%>
    <%--out.print(rootHtmlLevels);--%>
<%--%>--%>
    </div>
    <table class="table table-striped table-sm">
        <thead class="thead-inverse">
        <tr>
            <th width="50%">Logger</th>
            <th width="15%">Effective Level</th>
            <th width="35%">Change Log Level To</th>
        </tr>
        </thead>
        <tbody>
        <%
            // Render all loggers and sort them
            Set<Logger> sortedSet = new TreeSet<>(Comparator.comparing(Logger::getName));
            sortedSet.addAll(LoggerContext.getContext().getLoggers());
            for (Logger logger : sortedSet) {
                out.println("<tr>");

                out.print("<td><span title='");
                out.print("appenders="+logger.getAppenders().values());
                out.print("\ntoString="+logger.get().toString());
                out.print("'>");
                String name = StringUtils.isNotEmpty(logger.getName()) ? logger.getName() : "ROOT";
                out.print(name);
                out.print("</span></td>");

                out.print("<td>");
                out.print(logger.getLevel());
                out.print("</td>");

                out.print("<td>");
                StringBuilder htmlLevels = new StringBuilder();
                for (Level level : LOGGER_LEVELS) {
                    if (logger.getLevel() == level) {
                        htmlLevels
                                .append("<button class='btn btn-primary btn-sm active'");
                    }
                    else {
                        htmlLevels
                                .append("<a href='")
                                .append(request.getRequestURI())
                                .append("?")
                                .append("logger=").append(name)
                                .append("&level=").append(level.name())
                                .append("'>")
                                .append("<button type='submit' class='btn btn-secondary btn-sm'");
                    }

                    htmlLevels.append(" value='").append(level.intLevel()).append("'>").append(level.name()).append("</button>");

                    if (logger.getLevel() != level) {
                        htmlLevels.append("</a>");
                    }
                }
                out.print(htmlLevels);
                out.print("</td>");

                out.println("</tr>");
            }
        %>
        </tbody>
    </table>
</div>
</body>
<jsp:include page="_admin_footer.jsp"/>
</html>
