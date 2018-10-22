package org.achacha.webcardgame.web.v1;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * /api/auth/
 */
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthRoutes extends AbstractRoutes {
    private static final Logger LOGGER = LogManager.getLogger(AuthRoutes.class);

    @GET
    @Path("login")
    @SecurityLevelRequired(SecurityLevel.PUBLIC)
    public Response getLogin() {
        JsonObject obj = JsonHelper.getSuccessObject();

        LoginUserDbo user = CallContextTls.get().getLogin();
        if (user != null) {
            obj.add("user", user.toJsonObject());
            obj.addProperty("authenticated", true);
        }
        else {
            obj.add("user", null);
            obj.addProperty("authenticated", false);
        }

        return Response.ok().entity(obj).build();
    }

    /**
     * NOTE: /api/auth/login is a reserved URI (See GlobalProperties)
     * It is excluded from security rules as it is a way to authenticate
     *
     * @param email Email
     * @param password Password
     * @return Response
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("login")
    @SecurityLevelRequired(SecurityLevel.PUBLIC)
    public Response doLogin(@FormParam("email") String email, @FormParam("pwd") String password) {
        CallContext.LoginResult loginResult = CallContextTls.get().login(email, password);

        if (loginResult == CallContext.LoginResult.SUCCESS) {
            LOGGER.debug("Login success: email={}", email);
            JsonObject obj = JsonHelper.getSuccessObject();
            return Response.ok().entity(obj).build();
        }
        else {
            LOGGER.debug("Login fail: email={}", email);
            JsonObject obj = JsonHelper.getFailObject("login.fail", null);
            return Response.status(HttpServletResponse.SC_FORBIDDEN).entity(obj).build();
        }
    }

    /**
     * Impersonate existing user if current user is superuser
     * @param email to impersonate
     * @return Response
     */
    @PUT
    @Path("login")
    @SecurityLevelRequired(SecurityLevel.ADMIN)
    public Response impersonate(@QueryParam("email") String email) {
        Preconditions.checkState(StringUtils.isNotEmpty(email));

        CallContext ctx = CallContextTls.get();
        switch(ctx.impersonate(email)) {
            case LOGIN_IMPERSONATE:
                return Response.ok(JsonHelper.getSuccessObject()).build();
        }

        return Response.status(500).entity(JsonHelper.getFailObject("login.fail", null)).build();
    }

    /**
     * Logout currently logged in user
     * @return Response
     */
    @DELETE
    @Path("login")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response doLogout() {
        CallContextTls.get().logout();
        return Response.ok(JsonHelper.getSuccessObject()).build();

    }
}
