package org.achacha.webcardgame.web.v1;

import com.google.gson.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * /api/encounter
 */
@Path("encounter")
@Produces(MediaType.APPLICATION_JSON)
public class EncounterRoutes extends AbstractRoutes {
    private static final Logger LOGGER = LogManager.getLogger(EncounterRoutes.class);

    /**
     * Get available encounters for a given adventure
     */
    @GET
    @SecurityLevelRequired(SecurityLevel.AUTHENTICATED)
    public Response getEncounters(@QueryParam("adventureId") long id) {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            AdventureDbo adventure = Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).getById(connection, id);
            if (adventure != null) {
                JsonObject jobj = JsonHelper.getSuccessObject(adventure.getEncounters());
                jobj.addProperty("adventureId", id);
                return Response.ok(jobj).build();
            } else
                return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dbo.not.found", "Object not found, id=" + id)).build();
        } catch (SQLException e) {
            LOGGER.error("Failed to find encounters for adventureId="+id, e);
        }
        return Response.serverError().build();
    }
}
