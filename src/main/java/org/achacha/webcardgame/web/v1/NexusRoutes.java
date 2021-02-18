package org.achacha.webcardgame.web.v1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.NexusDbo;
import org.achacha.webcardgame.game.dbo.NexusDboFactory;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * /api/nexus
 */
@Path("nexus")
@Produces(MediaType.APPLICATION_JSON)
public class NexusRoutes extends AbstractRoutes {
    private static final Logger LOGGER = LogManager.getLogger(NexusRoutes.class);

    /**
     * Get nexus for player by ID
     * @return Response
     */
    @GET
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getNexus(@QueryParam("playerId") long playerId) {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, CallContextTls.get().getLogin().getId(), playerId);
            if (player == null)
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.not.found", "Object not found for playerId=" + playerId)).build();
            else {
                NexusDbo dbo = Global.getInstance().getDatabaseManager().<NexusDboFactory>getFactory(NexusDbo.class).getByPlayerId(connection, playerId);
                return Response.ok(JsonHelper.getSuccessObject(dbo)).build();
            }
        }
        catch(SQLException e) {
            LOGGER.error("Failed to get nexus for player", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
