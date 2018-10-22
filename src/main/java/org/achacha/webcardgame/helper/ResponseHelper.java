package org.achacha.webcardgame.helper;

import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * General handling of returns
 * Success - all is well
 * Fail - API call failed, server is ok
 * Error - Server error unable to execute API correctly
 */
public class ResponseHelper {
    private static final Logger LOGGER = LogManager.getLogger(ResponseHelper.class);

    /**
     * Do 302 redirect to url
     * @param response HttpServletResponse
     * @param url redirect URL
     */
    public static void redirectUrlVia302(HttpServletResponse response, String url) {
        LOGGER.debug("HTTP 302 redirect to URL: ", url);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", url);
    }

    /**
     * Do 302 redirect to uri and prepends context path
     * @param response HttpServletResponse
     * @param uri redirect URI
     */
    public static void redirectUriRelativeToContextVia302(HttpServletResponse response, String uri) {
        String url = Global.getInstance().getProperties().getWebContextPath() + uri;
        LOGGER.debug("HTTP 302 redirect to URL: ", url);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", url);
    }

    /**
     * Do 302 redirect to referer
     * @return true if referer exists and redirected, false if no redirect was done
     */
    public static boolean redirectToReferer() {
        CallContext context = CallContextTls.get();
        String referer = context.getRequest().getHeader("referer");
        if (null != referer) {
            context.getResponse().setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            context.getResponse().setHeader("Location", referer);
            return true;
        }
        return false;
    }

    /**
     * HTML format for HttpServletRequest
     * @param request HttpServletRequest
     * @return String
     */
    @SuppressWarnings("unchecked")
    public static String toText(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();

        //
        // HttpServletRequest
        //
        buffer.append("HttpServletRequest\n");

        // headers
        buffer.append("\n---HEADER---\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            buffer.append(name);
            buffer.append("=[");
            while (values.hasMoreElements()) {
                buffer.append(values.nextElement());
                if (values.hasMoreElements()) buffer.append(',');
            }
            buffer.append("],\n");
        }

        // parameters
        buffer.append("\n---PARAMETERS---\n");
        Enumeration<String> paramNames = request.getParameterNames();
        if (paramNames.hasMoreElements()) {
            while(paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                String[] values = request.getParameterValues(name);
                buffer.append(name);
                buffer.append("=[");
                buffer.append(StringUtils.join(values, ','));
                buffer.append("],\n");
            }
        }

        // attributes
        Enumeration<String> attrNames = request.getAttributeNames();
        if (attrNames.hasMoreElements()) {
            buffer.append("\n---ATTRIBUTES---\n");
            while(attrNames.hasMoreElements()) {
                String name = attrNames.nextElement();
                Object value = request.getAttribute(name);
                buffer.append(name);
                buffer.append("=[");
                buffer.append(value.toString());
                buffer.append("],\n");
            }
        }

        // session
        HttpSession session = request.getSession();
        buffer.append("\n---SESSION---\n");
        if (null != session) {
            // session attributes
            Enumeration<String> sattrNames = session.getAttributeNames();
            buffer.append("\n---SESSION-ATTRIBUTES---\n");
            if (sattrNames.hasMoreElements()) {
                while(sattrNames.hasMoreElements()) {
                    String name = sattrNames.nextElement();
                    Object value = session.getAttribute(name);
                    buffer.append(name);
                    buffer.append("=[");
                    buffer.append(value.toString());
                    buffer.append("]\n");
                }
            }
            buffer.append("id=[");
            buffer.append(session.getId());
            buffer.append("],\n");
            buffer.append("creationTime=[");
            buffer.append(DateFormatUtils.formatUTC(session.getCreationTime(), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()));
            buffer.append("],\n");
            buffer.append("lastAccessedTime=[");
            buffer.append(DateFormatUtils.formatUTC(session.getLastAccessedTime(), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()));
            buffer.append("],\n");
        }
        buffer.append("\n");

        // request members
        buffer.append("\nqueryString=[\n");
        buffer.append(StringUtils.defaultString(request.getQueryString()));
        buffer.append("],\nmethod=[");
        buffer.append(StringUtils.defaultString(request.getMethod()));
        buffer.append("],\nrequestURI=[");
        buffer.append(StringUtils.defaultString(request.getRequestURI()));
        buffer.append("],\ncontentType=[");
        buffer.append(StringUtils.defaultString(request.getContentType()));
        buffer.append("],\ncontentLength=[");
        buffer.append(request.getContentLength());
        buffer.append("],\nlocale=[");
        buffer.append(StringUtils.defaultString(request.getLocale().toString()));
        buffer.append("],\nprotocol=[");
        buffer.append(StringUtils.defaultString(request.getProtocol()));
        buffer.append("],\nreferer=[");
        buffer.append(StringUtils.defaultString(request.getRemoteAddr()));
        buffer.append("],\nremoteAddr=[");
        buffer.append(StringUtils.defaultString(request.getRemoteAddr()));
        buffer.append("],\nremoteHost=[");
        buffer.append(StringUtils.defaultString(request.getRemoteHost()));
        buffer.append("],\nremoteUser=[");
        buffer.append(StringUtils.defaultString(request.getRemoteUser()));
        buffer.append("],\nservletPath=[");
        buffer.append(StringUtils.defaultString(request.getServletPath()));
        buffer.append("],\ncontextPath=[");
        buffer.append(StringUtils.defaultString(request.getContextPath()));
        buffer.append("],\nauthType=[");
        buffer.append(StringUtils.defaultString(request.getAuthType()));
        buffer.append("],\nauthType=[");
        buffer.append(StringUtils.defaultString(request.getRemoteUser()));
        buffer.append("],\npathInfo=[");
        buffer.append(StringUtils.defaultString(request.getPathInfo()));
        buffer.append("],\n");

        return buffer.toString();
    }

    public static String toText(Cookie cookie) {
        return "name=" +
                StringUtils.defaultString(cookie.getName()) +
                "; value=" +
                StringUtils.defaultString(cookie.getValue()) +
                "; path=" +
                StringUtils.defaultString(cookie.getPath()) +
                "; domain=" +
                StringUtils.defaultString(cookie.getDomain()) +
                "; maxAge=" +
                cookie.getMaxAge() +
                "; comment=" +
                StringUtils.defaultString(cookie.getComment()) +
                "; secure=" +
                cookie.getSecure() +
                "\n";
    }

    public static String toText(HttpSession session) {
        StringBuilder buffer = new StringBuilder();
        if (null == session) {
            buffer.append("null");
        }
        else {
            buffer.append("session=").append(session.toString());
            buffer.append(" {\n");
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String value = session.getAttribute(name).toString();
                buffer.append("  ");
                buffer.append(name);
                buffer.append("=");
                buffer.append(value);
                buffer.append("\n");
            }
            buffer.append("}");
        }
        return buffer.toString();
    }

    public static String toHtml(CallContext ctx) {
        return "<h2>CallContext</h2>\n" +
                "<table class='table table-bordered table-sm'>" +
                "<tr><th>Method</th><td>" + ctx.getMethod() + "</td></tr>" +
                "<tr><th>BaseUrl</th><td>" + ctx.getBaseUrl() + "</td></tr>" +
                "<tr><th>Created Time</th><td>" + ctx.getCreatedTime() + "</td></tr>" +
                "</table>" +
                toHtml(ctx.getRequest());
    }

    /**
     * Create TABLE from 2 methods, one providing names other doing the name lookup
     * @param buffer StringBuilder
     * @param namesLister Supplier of Enumeration
     * @param resolver Resolves name into R type
     */
    private static <T, R> void appendEnumerationWithType(StringBuilder buffer, Supplier<Enumeration<T>> namesLister, Function<T, R> resolver) {
        Enumeration<T> names = namesLister.get();
        if (names.hasMoreElements()) {
            buffer.append("<table class='table table-bordered table-sm'>\n");

            while (names.hasMoreElements()) {
                T name = names.nextElement();
                buffer.append("<tr><th>").append(name).append("</th><td>").append(resolver.apply(name)).append("</td></tr>\n");
            }

            buffer.append("</table>\n");
        }
    }

    /**
     * Create TABLE from 2 methods, one providing names other doing the name lookup
     * @param buffer StringBuilder
     * @param namesLister Supplier of Enumeration
     * @param resolver Resolves name into array
     */
    private static <T> void appendEnumerationWithArray(StringBuilder buffer, Supplier<Enumeration<T>> namesLister, Function<T, T[]> resolver) {
        Enumeration<T> names = namesLister.get();
        if (names.hasMoreElements()) {
            buffer.append("<table class='table table-bordered table-sm'>\n");

            while (names.hasMoreElements()) {
                T name = names.nextElement();
                buffer.append("<tr><th>").append(name).append("</th><td><pre>");
                for(T value : resolver.apply(name)) {
                    buffer.append(value).append("\n");
                }

                buffer.append("</pre></td></tr>\n");
            }

            buffer.append("</table>\n");
        }
    }

    /**
     * Create TABLE from 2 methods, one providing names other doing the name lookup
     * @param buffer StringBuilder
     * @param namesLister Supplier of Enumeration
     * @param resolver Resolves name into Enumeration
     */
    private static <T> void appendEnumerationWithEnumeration(StringBuilder buffer, Supplier<Enumeration<T>> namesLister, Function<T, Enumeration<T>> resolver) {
        Enumeration<T> names = namesLister.get();
        if (names.hasMoreElements()) {
            buffer.append("<table class='table table-bordered table-sm'>\n");

            while (names.hasMoreElements()) {
                T name = names.nextElement();
                Enumeration<T> values = resolver.apply(name);

                buffer.append("<tr><th>").append(name).append("</th><td><pre>");
                while (values.hasMoreElements()) {
                    buffer.append(values.nextElement()).append("\n");
                }
                buffer.append("</pre></td></tr>\n");
            }

            buffer.append("</table>\n");
        }
    }

    /**
     * HTML format for HttpServletRequest
     * @param request HttpServletRequest
     * @return String
     */
    @SuppressWarnings("unchecked")
    public static String toHtml(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();

        //
        // HttpServletRequest
        //
        buffer.append("<h2>HttpServletRequest</h2>\n");

        // headers
        buffer.append("<table class='table table-bordered table-sm'><tr><th>headers</th><td>\n");
        appendEnumerationWithEnumeration(buffer, request::getHeaderNames, request::getHeaders);
        buffer.append("</td></tr>\n");

        // parameters
        buffer.append("<tr><th>parameters</th><td>");
        appendEnumerationWithArray(buffer, request::getParameterNames, request::getParameterValues);
        buffer.append("</td></tr>\n");

        // cookies
        Cookie[] cookies = request.getCookies();
        buffer.append("<tr><th>cookies</th><td>");
        if (null != cookies) {
            buffer.append("<table class='table table-bordered table-sm'>");
            for (Cookie cookie : cookies) {
                buffer.append("<tr><td>");
                buffer.append(toHtml(cookie));
                buffer.append("</td></tr>\n");
            }
            buffer.append("</table>");
        }
        buffer.append("</td></tr>\n");

        // attributes
        buffer.append("<tr><th>attributes</th><td>");
        appendEnumerationWithType(buffer, request::getAttributeNames, request::getAttribute);
        buffer.append("</td></tr>\n");

        // request members
        buffer.append("<tr><th>queryString</th><td>");
        buffer.append(StringUtils.defaultString(request.getQueryString()));
        buffer.append("</td></tr>\n<tr><th>method</th><td>");
        buffer.append(StringUtils.defaultString(request.getMethod()));
        buffer.append("</td></tr>\n<tr><th>requestURI</th><td>");
        buffer.append(StringUtils.defaultString(request.getRequestURI()));
        buffer.append("</td></tr>\n<tr><th>contentType</th><td>");
        buffer.append(StringUtils.defaultString(request.getContentType()));
        buffer.append("</td></tr>\n<tr><th>contentLength</th><td>");
        buffer.append(request.getContentLength());

        buffer.append("</td></tr>\n<tr><th>locale</th><td>");
        buffer.append(StringUtils.defaultString(request.getLocale().toString()));
        buffer.append("</td></tr>\n<tr><th>protocol</th><td>");
        buffer.append(StringUtils.defaultString(request.getProtocol()));

        buffer.append("</td></tr>\n<tr><th>remoteAddr</th><td>");
        buffer.append(StringUtils.defaultString(request.getRemoteAddr()));
        buffer.append("</td></tr>\n<tr><th>remoteHost</th><td>");
        buffer.append(StringUtils.defaultString(request.getRemoteHost()));
        buffer.append("</td></tr>\n<tr><th>remoteUser</th><td>");
        buffer.append(StringUtils.defaultString(request.getRemoteUser()));

        buffer.append("</td></tr>\n<tr><th>servletPath</th><td>");
        buffer.append(StringUtils.defaultString(request.getServletPath()));
        buffer.append("</td></tr>\n<tr><th>contextPath</th><td>");
        buffer.append(StringUtils.defaultString(Global.getInstance().getProperties().getWebContextPath()));

        buffer.append("</td></tr>\n<tr><th>authType</th><td>");
        buffer.append(StringUtils.defaultString(request.getAuthType()));
        buffer.append("</td></tr>\n<tr><th>authType</th><td>");
        buffer.append(StringUtils.defaultString(request.getRemoteUser()));
        buffer.append("</td></tr>\n<tr><th>pathInfo</th><td>");
        buffer.append(StringUtils.defaultString(request.getPathInfo()));

        // session
        HttpSession session = request.getSession(false);
        if (session != null) {
            buffer.append("<tr><th>session</th><td><table class='table table-bordered table-sm'>");
            // session attributes
            buffer.append("<tr><th>attributes</th><td>");
            appendEnumerationWithType(buffer, session::getAttributeNames, session::getAttribute);
            buffer.append("</td></tr>\n<tr><th>id</th><td>");
            buffer.append(session.getId());
            buffer.append("</td></tr>\n");
            buffer.append("<tr><th>creationTime</th><td>");
            buffer.append(DateFormatUtils.formatUTC(session.getCreationTime(), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()));
            buffer.append("</td></tr>\n");
            buffer.append("<tr><th>lastAccessedTime</th><td>");
            buffer.append(DateFormatUtils.formatUTC(session.getLastAccessedTime(), DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()));
            buffer.append("</td></tr>\n");
            buffer.append("</table></td></tr>\n");

            // servlet context
            ServletContext servletContext = session.getServletContext();
            if (servletContext != null) {
                buffer.append("<tr><th>servletContext</th><td><table class='table table-bordered table-sm'>");

                // servlet context init attributes
                buffer.append("<tr><th>initAttributes</th><td>");
                appendEnumerationWithType(buffer, servletContext::getInitParameterNames, servletContext::getInitParameter);
                buffer.append("</td>");
                buffer.append("<tr><th>contextPath</th><td>");
                buffer.append(servletContext.getContextPath());
                buffer.append("<tr><th>defaultSessionTrackingModes</th><td>");
                buffer.append(servletContext.getDefaultSessionTrackingModes());
                buffer.append("<tr><th>effectiveVersion</th><td>");
                buffer.append(servletContext.getEffectiveMajorVersion()).append(".").append(servletContext.getEffectiveMinorVersion());
                buffer.append("<tr><th>effectiveSessionTrackingModes</th><td>");
                buffer.append(servletContext.getEffectiveSessionTrackingModes());
                buffer.append("<tr><th>serverInfo</th><td>");
                buffer.append(servletContext.getServerInfo());
                buffer.append("<tr><th>version</th><td>");
                buffer.append(servletContext.getMajorVersion()).append(".").append(servletContext.getMinorVersion());
                buffer.append("<tr><th>virtualServerName</th><td>");
                buffer.append(servletContext.getVirtualServerName());
                buffer.append("</td></tr></table>\n");
            }

            buffer.append("</td></tr>");
        }
        else {
            buffer.append("<tr><th>session</th><td>null</td></tr>");
        }

        buffer.append("</table>\n");

        return buffer.toString();
    }

    public static String toHtml(Cookie cookie) {
        return "<b>name</b>=" +
                StringUtils.defaultString(cookie.getName()) +
                " &nbsp; <b>value</b>=" +
                StringUtils.defaultString(cookie.getValue()) +
                " &nbsp; <b>path</b>=" +
                StringUtils.defaultString(cookie.getPath()) +
                " &nbsp; <b>domain</b>=" +
                StringUtils.defaultString(cookie.getDomain()) +
                " &nbsp; <b>maxAge</b>=" +
                cookie.getMaxAge() +
                " &nbsp; <b>comment</b>=" +
                StringUtils.defaultString(cookie.getComment()) +
                " &nbsp; <b>secure</b>=" +
                cookie.getSecure() +
                "<br/>\n";
    }

    public static void expireCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, StringUtils.EMPTY);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Find a cookie by name in the request
     * @param request HttpServletRequest
     * @param name String
     * @return Cookie or null if not found
     */
    public static Cookie findRequestCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for(Cookie cookie : cookies)
                if (cookie.getName().equals(name))
                    return cookie;
        }
        return null;
    }

    /**
     * Redirect to the login page via 302 so URL can change to login page
     * @param context CallContext
     */
    public static void redirectToLogin(CallContext context) {
        String loginUrl = Global.getInstance().getProperties().getUrlPublicHome() + Global.getInstance().getProperties().getUriHomeLogin();
        LOGGER.debug("Redirecting to login URL={}", loginUrl);
        redirectUrlVia302(context.getResponse(), loginUrl);
    }

    /**
     * Redirect to JSP page internally
     * NOTE: This will keep requested URL unchanged
     * @param context CallContext
     * @param jspPath String
     * @throws ServletException if fails to forward
     * @throws IOException if fails forward
     */
    public static void redirectInternalToJsp(CallContext context, String jspPath) throws ServletException, IOException {
        LOGGER.debug("Servlet dispatcher redirect: {}", jspPath);
        context.getRequest().getRequestDispatcher(jspPath).forward(context.getRequest(), context.getResponse());
    }
}

