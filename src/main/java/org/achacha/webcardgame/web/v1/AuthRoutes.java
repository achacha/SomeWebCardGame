package org.achacha.webcardgame.web.v1;

import com.google.gson.JsonObject;
import org.achacha.base.security.SecurityHelper;
import org.achacha.webcardgame.helper.LoginHelper;
import org.achacha.webcardgame.helper.RoutesHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthRoutes {
    private static final Logger LOGGER = LogManager.getLogger(AuthRoutes.class);

    @POST
    @Path("login")
    public Response doLogin(@FormParam("email") String email, @FormParam("pwd") String password) {
        String hashPassword = SecurityHelper.encodeSaltPassword(password, email);
        if (LoginHelper.isAbleToLogIn(email, password)) {
            LOGGER.debug("Login success: email={} hashPassword={}", email, hashPassword);
            JsonObject obj = RoutesHelper.getSuccessObject();
            return Response.ok().entity(obj).build();
        }
        else {
            LOGGER.debug("Login fail: email={} hashPassword={}", email, hashPassword);
            JsonObject obj = RoutesHelper.getFailObject();
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(obj).build();
        }
    }
}
