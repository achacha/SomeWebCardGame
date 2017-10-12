package org.achacha.webcardgame.web.v1;

import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("player")
@Produces(MediaType.APPLICATION_JSON)
public class PlayerRoutes {
    @GET
    @Path("all")
    @SecurityLevelRequired(SecurityLevel.PUBLIC)
    public Response getAllPlayersForThisLogin() {
        CallContext ctx = CallContextTls.get();
        if (ctx == null || ctx.getLogin() == null)
            return Response.status(Response.Status.UNAUTHORIZED).entity(JsonHelper.getFailObject("Current user is not logged in")).build();
        else {
            Collection<PlayerDbo> players = PlayerDboFactory.getByLoginId(ctx.getLogin().getId());
            return Response.ok(JsonHelper.getSuccessWithData(players)).build();
        }
    }

    @GET
    @Path("{id}")
    @SecurityLevelRequired(SecurityLevel.PUBLIC)
    public Response getPlayer(@PathParam("id") long id) {
        PlayerDbo player = PlayerDboFactory.getByPlayerId(id);
        if (player == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("Object not found, id="+id)).build();
        else
            return Response.ok(JsonHelper.getSuccessWithData(player)).build();
    }
}
