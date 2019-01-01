package org.achacha.webcardgame.web.v1;

import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.AdventureArchiveDbo;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.achacha.webcardgame.game.logic.AdventureLogic;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
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

    private static final String SESSION_AVAILABLE = "available_adventures";

    /**
     * Get active adventure by playerId for this login
     */
    @GET
    @Path("active")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response activeAdventures(@QueryParam("playerId") long playerId) {
        // Get player by id for this login, if null then this login does not have such a player
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, CallContextTls.get().getLogin().getId(), playerId);
            if (player == null) {
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound", "Player not found, playerId=" + playerId)).build();
            }

            AdventureDbo existingAdventure = Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).getByPlayerId(connection, playerId);
            return Response.ok(JsonHelper.getSuccessObject(existingAdventure)).build();
        } catch (Exception e) {
            LOGGER.error("Failed to load active adventure", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activate an available adventure
     * @param playerId long
     * @param adventureId long negative value for adventure in session generated by available adventures call
     */
    @PUT
    @Path("active")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response startAdventure(@QueryParam("playerId") long playerId, @QueryParam("adventureId") int adventureId) {
        // Get player by id for this login, if null then this login does not have such a player
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, CallContextTls.get().getLogin().getId(), playerId);
            if (player == null)
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

            AdventureDbo existingAdventure = getActiveAdventure(playerId);
            if (existingAdventure != null) {
                return Response.status(Response.Status.FOUND).entity(existingAdventure).build();
            }

            List<AdventureDbo> adventures = (List<AdventureDbo>) httpRequest.getSession().getAttribute(SESSION_AVAILABLE);
            int index = -adventureId - 1;
            if (adventures == null || index < 0) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(JsonHelper.getFailObject("error.invalid.input.data", "Invalid session or index"))
                        .build();
            }

            AdventureDbo adventure = adventures.get(index);

            adventure.insert(connection);
            connection.commit();

            return Response.status(Response.Status.OK).entity(adventure).build();
        } catch (Exception e) {
            LOGGER.error("Failed to add active adventure", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete active adventure
     */
    @DELETE
    @Path("active")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response deleteActiveAdventures(@QueryParam("playerId") long playerId) {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, CallContextTls.get().getLogin().getId(), playerId);
            if (player == null)
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

            Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).deleteAllByPlayerId(connection, playerId);
            connection.commit();
        } catch (Exception e) {
            LOGGER.error("Failed to delete active adventure", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok().build();
    }

    /**
     * Get available adventures for a given player
     * @param playerId player to use for generation
     * @param regenerate if true will ignore the adventures in the session and create new ones
     */
    @GET
    @Path("available")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response availableAdventures(@QueryParam("playerId") long playerId, @QueryParam("regenerate") boolean regenerate) {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            // Get player by id for this login, if null then this login does not have such a player
            PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, CallContextTls.get().getLogin().getId(), playerId);
            if (player == null)
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound", "Player not found, playerId=" + playerId)).build();

            // Check if player is already on an adventure
            AdventureDbo existingAdventure = getActiveAdventure(playerId);
            if (existingAdventure != null) {
                return Response.status(Response.Status.FOUND).entity(existingAdventure).build();
            }

            List<AdventureDbo> adventures = (List<AdventureDbo>) httpRequest.getSession().getAttribute(SESSION_AVAILABLE);
            if (adventures == null || regenerate) {
                final int LEVEL = player.getCards().stream().map(CardDbo::getLevel).max(Integer::compare).orElse(0);
                final int COUNT = RandomUtils.nextInt(2, 5);
                adventures = new ArrayList<>(COUNT);
                // Use negative id since it is not persisted
                for (int tempId = 1; tempId <= COUNT; ++tempId) {
                    // Randomize
                    AdventureDbo adventure = AdventureDbo.builder(player.getId()).build();
                    adventure.getEncounters().add(EncounterDbo.builder(adventure).withRandomGeneratedCards(LEVEL, RandomUtils.nextInt(1,3)).build());
                    adventure.setId(-tempId);
                    adventures.add(adventure);

                }
                httpRequest.getSession().setAttribute(SESSION_AVAILABLE, adventures);
                LOGGER.debug("Generated new adventures={}", adventures);
            }

            return Response.status(Response.Status.OK).entity(JsonHelper.getSuccessObject(adventures)).build();
        }
        catch (Exception e) {
            LOGGER.error("Failed to get available adventures", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get active adventure
     * @param playerId long
     * @return AdventureDbo
     */
    @Nullable
    private AdventureDbo getActiveAdventure(long playerId) {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            // Check if player is already on an adventure
            return Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).getByPlayerId(connection, playerId);
        } catch (Exception e) {
            LOGGER.error("Failed to load active adventure", e);
            throw new RuntimeException("Failed to load active adventure", e);
        }
    }

    /**
     * Simulate adventure
     */
    @PUT
    @Path("simulate")
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response simulateAdventure(@QueryParam("playerId") long playerId) {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            // Get player by id for this login, if null then this login does not have such a player
            PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, CallContextTls.get().getLogin().getId(), playerId);
            if (player == null)
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound","Player not found, playerId="+playerId)).build();

            AdventureDbo adventure = getActiveAdventure(playerId);
            if (adventure != null) {
                AdventureArchiveDbo adventureArchive = AdventureLogic.simulateAdventure(connection, player, adventure);
                connection.commit();

                return Response.status(Response.Status.OK).entity(JsonHelper.getSuccessObject(adventureArchive)).build();
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.notfound", "Adventure not found, playerId=" + playerId)).build();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load active adventure", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
