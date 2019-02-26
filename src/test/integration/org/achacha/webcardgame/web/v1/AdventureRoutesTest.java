package org.achacha.webcardgame.web.v1;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.test.BaseIntegrationTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.AdventureArchiveDbo;
import org.achacha.webcardgame.game.dbo.AdventureArchiveDboFactory;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdventureRoutesTest extends BaseIntegrationTest {
    @Test
    void adventureUsecase() throws Exception {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_USER_EMAIL, TestDataConstants.JUNIT_USER_PASSWORD)) {
            // Verify active player does not have adventure
            final Page pageNoActive = webClient.getPage(getUrl("/api/adventure/active?playerId="+ TestDataConstants.JUNIT_PLAYER__ID));
            WebResponse wrNoActive = pageNoActive.getWebResponse();
            assertEquals(Response.Status.OK.getStatusCode(), wrNoActive.getStatusCode());
            JsonObject json = parseContentJsonObject(wrNoActive);
            assertFalse(json.has(JsonHelper.DATA));    // Would contain no data if no adventure is active for this player

            // Get available adventures
            final Page pageAvailable = webClient.getPage(getUrl("/api/adventure/available?playerId="+ TestDataConstants.JUNIT_PLAYER__ID));
            WebResponse wrAvailable = pageAvailable.getWebResponse();
            assertEquals(Response.Status.OK.getStatusCode(), wrAvailable.getStatusCode());
            json = parseContentJsonObject(wrAvailable);
            assertTrue(json.has(JsonHelper.DATA));
            JsonArray ary = json.get(JsonHelper.DATA).getAsJsonArray();
            assertTrue(ary.size() > 0);

            // Save the available adventure
            JsonObject adventureAvailableJson = ary.get(0).getAsJsonObject();
            AdventureDbo adventureAvailable = Global.getInstance().getGson().fromJson(adventureAvailableJson, AdventureDbo.class);

            // Activate adventure
            WebRequest wrActivateAdventure = new WebRequest(
                    getUrl(
                            "/api/adventure/active?adventureId="+adventureAvailable.getId()+
                            "&playerId="+TestDataConstants.JUNIT_PLAYER__ID+
                            "&cards=2,1,0"
                    ),
                    HttpMethod.PUT
            );
            final Page pageActivateAdventure = webClient.getPage(wrActivateAdventure);
            WebResponse weActivateAdventure = pageActivateAdventure.getWebResponse();
            assertEquals(Response.Status.OK.getStatusCode(), weActivateAdventure.getStatusCode());

            // Verify active player now has an adventure
            final Page pageVerifyActive = webClient.getPage(getUrl("/api/adventure/active?playerId="+ TestDataConstants.JUNIT_PLAYER__ID));
            WebResponse wrVerifyActive = pageVerifyActive.getWebResponse();
            assertEquals(Response.Status.OK.getStatusCode(), wrVerifyActive.getStatusCode());
            json = parseContentJsonObject(wrVerifyActive);
            assertTrue(json.has(JsonHelper.DATA));
            AdventureDbo adventureSelected = Global.getInstance().getGson().fromJson(json.get(JsonHelper.DATA), AdventureDbo.class);
            assertEquals(
                    adventureAvailable.getEncounters().get(0).getEnemies().get(0).getName(),
                    adventureSelected.getEncounters().get(0).getEnemies().get(0).getName()
            );

            // Simulate
            WebRequest wreqSimulateAdventure = new WebRequest(
                    getUrl("/api/adventure/simulate?adventureId="+adventureAvailable.getId()+"&playerId="+TestDataConstants.JUNIT_PLAYER__ID),
                    HttpMethod.PUT
            );
            final Page pageSimulateAdventure = webClient.getPage(wreqSimulateAdventure);
            WebResponse wresSimulateAdventure = pageSimulateAdventure.getWebResponse();
            assertEquals(Response.Status.OK.getStatusCode(), wresSimulateAdventure.getStatusCode());
            json = parseContentJsonObject(wresSimulateAdventure);
            assertTrue(json.has(JsonHelper.DATA));
            assertEquals(AdventureArchiveDbo.class.getName(), json.get(JsonHelper.DATA_CLASS).getAsString());
            AdventureArchiveDbo adventureArchive = Global.getInstance().getGson().fromJson(json.get(JsonHelper.DATA), AdventureArchiveDbo.class);
            assertNotNull(adventureArchive);
            assertEquals(adventureSelected.getId(), adventureArchive.getOriginalId());

            // Verify no active adventures exists after simulation
            final Page pageVerifyNoActive = webClient.getPage(getUrl("/api/adventure/active?playerId="+ TestDataConstants.JUNIT_PLAYER__ID));
            WebResponse wrVerifyNoActive = pageVerifyNoActive.getWebResponse();
            assertEquals(Response.Status.OK.getStatusCode(), wrVerifyNoActive.getStatusCode());
            json = parseContentJsonObject(wrVerifyNoActive);
            assertFalse(json.has(JsonHelper.DATA));

            // Verify it was moved to archive by checking directly in DB
            try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
                AdventureArchiveDbo adventureArchiveInDb = Global.getInstance().getDatabaseManager().<AdventureArchiveDboFactory>getFactory(AdventureArchiveDbo.class)
                        .getByOriginalId(connection, adventureArchive.getOriginalId());
                assertNotNull(adventureArchiveInDb);
                assertEquals(adventureArchive.getId(), adventureArchiveInDb.getId());
                assertEquals(adventureSelected.getId(), adventureArchiveInDb.getOriginalId());
            }


       }
    }
}
