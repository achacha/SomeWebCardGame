package org.achacha.webcardgame.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.global.GlobalProperties;
import org.achacha.webcardgame.helper.LoginHelper;
import org.achacha.webcardgame.helper.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter(filterName = "RootAuthFilter", value = "/*")
public class RootFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootFilter.class);

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        boolean contextSet = false;
        long startTime = System.nanoTime();
        String logMessage = "NONE";
        try {
            // Check if this is a request for login, if not make sure security is enforced
            String uri = CallContext.getUriRelativeToWebContext(request);
            if (LOGGER.isDebugEnabled()) {
                logMessage = ((HttpServletRequest) req).getMethod() + "(" + uri + ")";
                LOGGER.debug("Processing: {}", logMessage);
            }
            else
                logMessage = uri;

            // Create CallContext for non-static calls
            if (!LoginHelper.isStaticUri(uri)) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Creating CallContext for "+logMessage);

                // Put CallContext into TLS
                CallContext context = new CallContext(request, (HttpServletResponse) resp, request.getMethod());
                CallContextTls.set(context);
                contextSet = true;

                // Anything starting with /api/ must have a user
                LoginUserDbo user = CallContextTls.get().getLogin();
                if (user == null && !LoginHelper.isLoginTargetUri(uri)) {
                    LOGGER.debug("Must have a user to access, user={} {}", user, logMessage);
                    LoginHelper.processOriginatingUri(request);
                    ResponseHelper.redirectToLogin(context);
                }

                // First check if admin is being accessed
                if (uri.startsWith(GlobalProperties.URI_ADMIN)) {
                    if (user == null || !user.isSuperuser()) {
                            LOGGER.debug("/admin requires a logged in superuser, for user={} {}", user, logMessage);
                        LoginHelper.processOriginatingUri(request);
                        ResponseHelper.redirectToLogin(context);
                    }
                }
            }
            else {
                LOGGER.debug("Skipping CallContext for {}", logMessage);
            }

            // Continue on to other filter chains, authetication will be done by the auth filter further down
            chain.doFilter(req, resp);
        }
        finally {
            HttpServletResponse response = (HttpServletResponse)resp;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Finished processing {}  status={}  t={}us", logMessage, response.getStatus(), (System.nanoTime() - startTime)/1000);
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
