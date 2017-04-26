package org.achacha.webcardgame.web.filter;

import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonObject;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.EventLogDboFactory;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.global.Global;
import org.achacha.base.global.GlobalProperties;
import org.achacha.base.logging.Event;
import org.achacha.base.web.ServletHelper;
import org.achacha.webcardgame.helper.LoginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "RootAuthFilter", value = "/*")
public class RootAuthFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootAuthFilter.class);

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        boolean contextSet = false;
        long startTime = System.nanoTime();
        try {
            // Check if this is a request for login, if not make sure security is enforced
            String uri = CallContext.getUriRelativeToWebContext(request);
            LOGGER.debug("Processing relative URI: {}", uri);
            if (!LoginHelper.isPublicUri(uri)) {
                // MUST be logged in
                boolean needsLogin = true;
                HttpSession session = request.getSession();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(ServletHelper.toText(session));
                }
                LoginUserDbo login = null;
                if (null != session) {
                    login = CallContext.fromSession(session);
                    if (null != login) {
                        needsLogin = false;
                    }
                }

                // Session or Login does not exist, redirect to login page
                if (!LoginHelper.isLoginTargetUri(uri) && needsLogin) {
                    // GET referer and save URL that requested this
                    String baseOriginatingUrl = request.getParameter(HttpHeaders.REFERER);
                    if (baseOriginatingUrl == null && request.getSession() != null && request.getSession().getAttribute(CallContext.SESSION_REDIRECT_FROM) == null) {
                        // We do not have a referer and we do not have a target to redirect to, use originating URI
                        baseOriginatingUrl = request.getRequestURI();
                    }
                    if (baseOriginatingUrl != null) {
                        // We have a URL to return to
                        StringBuilder originatingUrl = new StringBuilder(baseOriginatingUrl);
                        if (null != request.getQueryString()) {
                            originatingUrl.append('?');
                            originatingUrl.append(request.getQueryString());
                        }
                        request.getSession(true).setAttribute(CallContext.SESSION_REDIRECT_FROM, originatingUrl.toString());
                    }
                    ServletHelper.redirectUriRelativeToContextVia302((HttpServletResponse) resp, Global.getInstance().getProperties().getUriHomeLogin());
                    return;
                }

                Preconditions.checkNotNull(login, "Must have a valid login object if not redirecting to login page");

                // MUST be admin to access /admin part of the site
                if (uri.startsWith(GlobalProperties.URI_ADMIN) && !login.isSuperuser()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Superuser check failed for /admin, redirecting to home, URI: '{}' for user id={}", request.getRequestURI(), login.getId());
                    }

                    // Log invalid access
                    JsonObject errorObject = new JsonObject();
                    errorObject.add("login", login.toJsonObject());
                    errorObject.addProperty("uri", uri);
                    EventLogDboFactory.insertInternal(Event.LOGIN_PERMISSION_INVALID, errorObject);
                    ServletHelper.redirectUriRelativeToContextVia302((HttpServletResponse) resp, Global.getInstance().getProperties().getUriHome());
                    return;
                }
            }

            // Create CallContext for non-static calls
            if (!LoginHelper.isStaticUri(uri)) {
                LOGGER.debug("Creating CallContext for {}", uri);
                // Put CallContext into TLS
                CallContext context = new CallContext(request, (HttpServletResponse) resp, request.getMethod());
                CallContextTls.set(context);
                contextSet = true;
            }
            else {
                LOGGER.debug("Skipping CallContext for {}", uri);
            }

            // Continue on to other filter chains
            chain.doFilter(req, resp);
        }
        finally {
            HttpServletResponse response = (HttpServletResponse)resp;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Finished processing URI={}  status={}  t={}us", request.getRequestURI(), response.getStatus(), (System.nanoTime() - startTime)/1000);
            }

            // Context was set on Tls in this scope, clean it up for consistency
            if (contextSet) {
                CallContextTls.unset();
            }
        }
    }

    public void init(FilterConfig config) throws ServletException {}
    public void destroy() {}
}
