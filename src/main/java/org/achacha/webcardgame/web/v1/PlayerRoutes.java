package org.achacha.webcardgame.web.v1;

import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * /api/player
 */
@Path("player")
@Produces(MediaType.APPLICATION_JSON)
public class PlayerRoutes extends AbstractRoutes {
    /**
     * Get players associated with this login
     * @return Response
     */
    @GET
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getAllPlayersForThisLogin() {
        Collection<PlayerDbo> players = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginId(CallContextTls.get().getLogin().getId());
        return Response.ok(JsonHelper.getSuccessObject(players)).build();
    }

    /**
     * Get player by ID for this login
     * @param id long
     * @return Response
     */
    @GET
    @Path("{id}")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getPlayer(@PathParam("id") long id) {
        PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(CallContextTls.get().getLogin().getId(), id);
        if (player == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.not.found", "Object not found, id="+id)).build();
        else
            return Response.ok(JsonHelper.getSuccessObject(player)).build();
    }
}
