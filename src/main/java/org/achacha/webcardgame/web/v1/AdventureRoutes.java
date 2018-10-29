package org.achacha.webcardgame.web.v1;

import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * /api/adventure
 */
@Path("adventure")
@Produces(MediaType.APPLICATION_JSON)
public class AdventureRoutes extends AbstractRoutes {
    /**
     * Get adventure by playerId for this login
     */
    @GET
    @Path("active")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getActiveAdventures(@QueryParam("playerId") long playerId) {
        // Get player by id for this login, if null then this login does not have such a player
        PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(CallContextTls.get().getLogin().getId(), playerId);
        if (player == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

        List<AdventureDbo> adventures = Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).getByPlayerId(player.getId());
        if (adventures == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound", "Adventures not found for playerId="+playerId)).build();


        return Response.ok(JsonHelper.getSuccessObject(adventures)).build();
    }

    /**
     * Get available adventures for a given player
     */
    @GET
    @Path("available")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getAvailableAdventures(@QueryParam("level") int level) {
        AdventureDbo dbo = AdventureDbo.builder(3, level).build();
        //TODO: What else do we need?
        return Response.status(Response.Status.NOT_IMPLEMENTED).entity(dbo).build();
    }
}
