package org.achacha.webcardgame.web.v1;

import com.google.gson.JsonObject;
import org.achacha.webcardgame.db.Factory;
import org.achacha.webcardgame.dbo.Login;
import org.achacha.webcardgame.helper.RoutesHelper;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
public class ServerRoutes {
    private static final Log LOGGER = LogFactory.getLog(ServerRoutes.class);

    @GET
    @Path("status")
    public Response getStatus() {
        return Response.ok(RoutesHelper.getSuccessObject()).build();
    }

    @GET
    @Path("data")
    public Response getData() {
        try (Session session = Factory.getInstance().getSessionFactory().openSession()) {
            Login login = session.get(Login.class, 1L);

            JsonObject obj = new JsonObject();
            if (login != null) {
                obj.addProperty("id", login.getId());
                obj.addProperty("email", login.getEmail());
            }
            return Response.ok(obj.toString()).build();
        }
    }

}
