package org.achacha.webcardgame.web.v1;

import com.google.gson.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.MyApplication;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
public class ServerRoutes extends AbstractRoutes {
    @Inject
    MyApplication application;

    @GET
    @Path("status")
    @SecurityLevelRequired(SecurityLevel.PUBLIC)
    public Response getStatus() {
        CallContext callContext = CallContextTls.get();

        JsonObject obj = JsonHelper.getSuccessObject();
        obj.add("build", Global.getBuildVersion());
        obj.addProperty("httpRequest", httpRequest.toString());
        obj.addProperty("requestContext", requestContext.toString());
        obj.addProperty("application", application.toString());
        obj.addProperty("callContext", callContext.toString());
        return Response.ok(obj).build();
    }

    @GET
    @Path("isuser")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getIfUser() {
        JsonObject obj = JsonHelper.getSuccessObject();
        obj.addProperty("authenticated", true);
        return Response.ok(obj).build();
    }

    @GET
    @Path("isadmin")
    @SecurityLevelRequired(SecurityLevel.ADMIN)
    public Response getIfAdmin() {
        JsonObject obj = JsonHelper.getSuccessObject();
        obj.addProperty("authenticated", true);
        obj.addProperty("admin", true);
        return Response.ok(obj).build();
    }
}
