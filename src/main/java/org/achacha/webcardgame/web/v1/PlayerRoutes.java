package org.achacha.webcardgame.web.v1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * /api/player
 */
@Path("player")
@Produces(MediaType.APPLICATION_JSON)
public class PlayerRoutes extends AbstractRoutes {
    private static final Logger LOGGER = LogManager.getLogger(PlayerRoutes.class);

    /**
     * Get players associated with this login
     * @return Response
     */
    @GET
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getAllPlayersForThisLogin() {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            Collection<PlayerDbo> players = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginId(connection, CallContextTls.get().getLogin().getId());
            return Response.ok(JsonHelper.getSuccessObject(players)).build();
        }
        catch(SQLException e) {
            LOGGER.error("Failed tp get all players for login", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
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
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, CallContextTls.get().getLogin().getId(), id);
            if (player == null)
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.not.found", "Object not found, id=" + id)).build();
            else
                return Response.ok(JsonHelper.getSuccessObject(player)).build();
        }
        catch(SQLException e) {
            LOGGER.error("Failed tp get all players for login", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
