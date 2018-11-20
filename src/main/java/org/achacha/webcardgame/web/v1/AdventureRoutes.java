package org.achacha.webcardgame.web.v1;

import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * /api/adventure
 */
@Path("adventure")
@Produces(MediaType.APPLICATION_JSON)
public class AdventureRoutes extends AbstractRoutes {
    private static final Logger LOGGER = LogManager.getLogger(AdventureRoutes.class);

    /**
     * Get active adventure by playerId for this login
     */
    @GET
    @Path("active")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getActiveAdventures(@QueryParam("playerId") long playerId) {
        // Get player by id for this login, if null then this login does not have such a player
        PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(CallContextTls.get().getLogin().getId(), playerId);
        if (player == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

        AdventureDbo existingAdventure = Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).getByPlayerId(playerId);
        if (existingAdventure == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound", "Adventures not found for playerId="+playerId)).build();


        return Response.ok(JsonHelper.getSuccessObject(existingAdventure)).build();
    }

    /**
     * Get available adventures for a given player
     */
    @GET
    @Path("available")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getAvailableAdventures(@QueryParam("playerId") long playerId) {
        // Get player by id for this login, if null then this login does not have such a player
        PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(CallContextTls.get().getLogin().getId(), playerId);
        if (player == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

        // Check if player is already on an adventure
        AdventureDbo existingAdventure = Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).getByPlayerId(playerId);
        if (existingAdventure != null) {
            return Response.status(Response.Status.FOUND).entity(existingAdventure).build();
        }

        final int LEVEL = player.getCards().stream().map(CardDbo::getLevel).max(Integer::compare).orElse(0);
        final int COUNT = RandomUtils.nextInt(2,5);
        List<AdventureDbo> adventures = new ArrayList<>(COUNT);
        // Use negative id since it is not persisted
        for (int tempId = 1; tempId <= COUNT; ++tempId){
            AdventureDbo dbo = AdventureDbo.builder(3, LEVEL).build();
            dbo.setId(-tempId);
            adventures.add(dbo);

        }
        httpRequest.getSession().setAttribute("available_adventures", adventures);

        return Response.status(Response.Status.OK).entity(adventures).build();
    }

    @PUT
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response selectAdventure(@QueryParam("playerId") long playerId, @QueryParam("adventureId") int adventureId) {
        // Get player by id for this login, if null then this login does not have such a player
        PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(CallContextTls.get().getLogin().getId(), playerId);
        if (player == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

        // Check if player is already on an adventure
        AdventureDbo existingAdventure = Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).getByPlayerId(playerId);
        if (existingAdventure != null) {
            return Response.status(Response.Status.FOUND).entity(existingAdventure).build();
        }

        List<AdventureDbo> adventures = (List<AdventureDbo>) httpRequest.getSession().getAttribute("available_adventures");
        int index = -adventureId - 1;
        if (adventures == null || adventureId < 0 || index < 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        AdventureDbo adventure = adventures.get(index);

        //TODO:
        // 1. Create PUT to accept adventure
        // 1. Allow view of 'active' adventures
        // 1. Allow 'simulate'

        // TODO: Save
        adventure.setActive(true);
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            adventure.insert(connection);
            connection.commit();
        } catch (Exception e) {
            LOGGER.error("Failed to add active adventure="+adventure, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(adventure).build();
        }

        return Response.status(Response.Status.OK).entity(adventure).build();
    }

    /**
     * Simulate adventure
     */
    @GET
    @Path("simulate")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response simulateAdventure(@QueryParam("playerId") long playerId) {
        // Get player by id for this login, if null then this login does not have such a player
        PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(CallContextTls.get().getLogin().getId(), playerId);
        if (player == null)
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
