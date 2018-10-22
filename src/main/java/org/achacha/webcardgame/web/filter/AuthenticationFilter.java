package org.achacha.oddity.web.filter;

import com.google.common.net.HttpHeaders;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.helper.LoginHelper;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

/*
Jersey bug does not allow injecting request from context into a filter
Using (HttpServletRequest) requestContext.getRequest() works
This is probably due to request not being fully available at this point
https://github.com/jersey/jersey/issues/3422
*/
@SecurityLevelRequired
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Check method security
        Method method = resourceInfo.getResourceMethod();
        SecurityLevelRequired[] levelsRequired = method.getAnnotationsByType(SecurityLevelRequired.class);
        if (levelsRequired.length > 0) {
            // Has annotation to process
            SecurityLevel minimumRequiredLevel = levelsRequired[0].value();
            boolean requiresSuperuser = levelsRequired[0].requiresSuperuser();
            if (!minimumRequiredLevel.isPublic()) {
                // Not a public level, so validate authenticated user
                LoginUserDbo login = CallContextTls.get().getLogin();
                if (login == null) {
                    // Redirect to a login page
                    LOGGER.debug("No user logged in for URI={}  securityLevelRequired={}, redirecting to login", requestContext.getUriInfo().getRequestUri(), minimumRequiredLevel);
                    redirectToLoginPage(requestContext);
                } else {
                    // Verify security level
                    if ((requiresSuperuser && !login.isSuperuser()) ||
                            !login.getSecurityLevel().isLevelSufficient(minimumRequiredLevel)) {
                        LOGGER.debug("Authorization too low for URI={}  login={}  securityLevelRequired={}  requiresSuperuser=", requestContext.getUriInfo().getRequestUri(), login, minimumRequiredLevel, requiresSuperuser);
                        requestContext.abortWith(
                                Response.status(Response.Status.FORBIDDEN)
                                        .entity(JsonHelper.getFailObject("error.security.forbidden", null))
                                        .build()
                        );
                    }
                }
            }
        }

        LOGGER.debug("Authorization valid for URI={}", requestContext.getUriInfo().getRequestUri());
    }
    private void redirectToLoginPage(ContainerRequestContext requestContext) {
        // GET referer and save URL that requested this
        LoginHelper.processOriginatingUri((HttpServletRequest) requestContext.getRequest());
        requestContext.abortWith(
                Response.status(Response.Status.FOUND)
                        .header(HttpHeaders.LOCATION, Global.getInstance().getProperties().getUriHomeLogin())
                        .build()
        );
    }
}