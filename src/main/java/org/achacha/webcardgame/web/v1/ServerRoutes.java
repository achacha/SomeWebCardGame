package org.achacha.webcardgame.web.v1;

import org.achacha.webcardgame.helper.RoutesHelper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
public class ServerRoutes {
    @GET
    @Path("status")
    public Response getStatus() {
        return Response.ok(RoutesHelper.getSuccessObject()).build();
    }

    @GET
    @Path("data")
    public Response getData() {
        return Response.ok().build();
    }

}
