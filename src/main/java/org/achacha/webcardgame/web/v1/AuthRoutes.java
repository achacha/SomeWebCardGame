package org.achacha.webcardgame.web.v1;

import com.google.gson.JsonObject;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.helper.LoginHelper;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthRoutes {
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
        String hashPassword = SecurityHelper.encodeSaltPassword(password, email);
        if (LoginHelper.isAbleToLogIn(email, hashPassword)) {
            LOGGER.debug("Login success: email={} hashPassword={}", email, hashPassword);
            JsonObject obj = JsonHelper.getSuccessObject();
            return Response.ok().entity(obj).build();
        }
        else {
            LOGGER.debug("Login fail: email={} hashPassword={}", email, hashPassword);
            JsonObject obj = JsonHelper.getFailObject("Failed to authenticate user");
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(obj).build();
        }
    }
}
