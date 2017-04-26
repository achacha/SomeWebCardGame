package org.achacha.webcardgame.web.filter;

import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.helper.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
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
                    LOGGER.debug("No user logged in for URI: {}  securityLevelRequired: {}", requestContext.getUriInfo().getRequestUri(), minimumRequiredLevel);
                    requestContext.abortWith(ResponseHelper.getAuthFailed("No logged in user"));
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
}