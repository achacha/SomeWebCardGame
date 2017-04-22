package org.achacha.webcardgame.web.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthRoutes {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRoutes.class);

    @POST
    @Path("login")
    public void doLogin(@FormParam("u") String username, @FormParam("p") String password) {
        LOGGER.info("Login "+username+":"+password);
    }
}