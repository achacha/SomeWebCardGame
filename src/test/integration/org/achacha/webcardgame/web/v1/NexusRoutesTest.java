package org.achacha.webcardgame.web.v1;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.gson.JsonObject;
import jakarta.ws.rs.core.Response;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.test.BaseIntegrationTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.NexusDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.achacha.test.TestHelper.createNewTestPlayer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NexusRoutesTest extends BaseIntegrationTest {
    @Test
    void nexusUsecase() throws Exception {
        // Create a new player without nexus
        PlayerDbo player = createNewTestPlayer("nexusRoutes1");

        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_USER_EMAIL, TestDataConstants.JUNIT_USER_PASSWORD)) {
            {
                // Verify that player by default doesn't have a nexus
                final Page pageNoNexus = webClient.getPage(getUrl("/api/nexus?playerId=" + player.getId()));
                WebResponse wrNoNexus = pageNoNexus.getWebResponse();
                assertEquals(Response.Status.OK.getStatusCode(), wrNoNexus.getStatusCode());
                JsonObject json = parseContentJsonObject(wrNoNexus);
                assertFalse(json.has(JsonHelper.DATA));    // Would contain no data if no nexus for this player
            }

            // Create new nexus for this player
            NexusDbo nexus = new NexusDbo();
            nexus.setPlayerId(player.getId());
            try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
                nexus.insert(connection);
                connection.commit();
            }

            {
                // Verify that player now has a nexus
                final Page pageNexus = webClient.getPage(getUrl("/api/nexus?playerId=" + player.getId()));
                WebResponse wrNexus = pageNexus.getWebResponse();
                assertEquals(Response.Status.OK.getStatusCode(), wrNexus.getStatusCode());
                JsonObject json = parseContentJsonObject(wrNexus);
                assertTrue(json.has(JsonHelper.DATA));    // Would contain no data if no nexus for this player
                assertEquals(player.getId(), nexus.getPlayerId());

                JsonObject data = json.getAsJsonObject("data");
                assertEquals(player.getId(), data.get("playerId").getAsLong());
            }
       }
    }
}
