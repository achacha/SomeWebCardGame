package org.achacha.webcardgame.web.filter;

import com.google.common.net.HttpHeaders;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.global.Global;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.helper.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@SecurityLevelRequired
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    // TODO: Maybe add a way to inject LoginUserDbo into Context?

    @Context
    private ResourceInfo resourceInfo;

    @Context
    HttpServletRequest request;

    @Context
    HttpServletResponse response;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        SecurityLevelRequired[] levelsRequired = method.getAnnotationsByType(SecurityLevelRequired.class);
        if (levelsRequired.length > 0) {
            // Has annotation to process
            SecurityLevel minimumRequiredLevel = levelsRequired[0].value();
            if (!minimumRequiredLevel.isPublic()) {
                // Not a public level, so validate authenticated user
                LoginUserDbo user = CallContextTls.get().getLogin();
                if (user == null) {
                    // Redirect to a login page
                    LOGGER.debug("No user logged in for URI: {}  securityLevelRequired: {}, redirecting to login", requestContext.getUriInfo().getRequestUri(), minimumRequiredLevel);
                    redirectToLoginPage(requestContext);
                } else {
                    if (!user.getSecurityLevel().isLevelSufficient(minimumRequiredLevel)) {
                        LOGGER.debug("Authorization too low for URI: {}  securityLevelRequired: {}", requestContext.getUriInfo().getRequestUri(), minimumRequiredLevel);
                        requestContext.abortWith(ResponseHelper.getAuthFailed("Insufficient security level"));
                    }
                }
            }
        }

        LOGGER.debug("Authorization valid for URI: {}", requestContext.getUriInfo().getRequestUri());
    }

    private void redirectToLoginPage(ContainerRequestContext requestContext) {
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
        requestContext.abortWith(
                Response.status(HttpServletResponse.SC_FOUND)
                        .header(HttpHeaders.LOCATION, Global.getInstance().getProperties().getUriHomeLogin()
                ).build()
        );
    }
}