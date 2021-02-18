package org.achacha.webcardgame.web.v1.admin;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.gson.JsonObject;
import jakarta.ws.rs.core.Response;
import org.achacha.test.BaseIntegrationTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DaoRoutesTest extends BaseIntegrationTest {
    @Test
    void testLoginWithNonAdminAccess() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_USER_EMAIL, TestDataConstants.JUNIT_USER_PASSWORD)) {
            final WebResponse response = webClient.loadWebResponse(new WebRequest(getUrl("/api/admin/dbo/all")));
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatusCode());
        }
    }

    @Test
    void testGetAllDao() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_ADMIN_EMAIL, TestDataConstants.JUNIT_ADMIN_PASSWORD)) {
            final WebResponse response = webClient.loadWebResponse(new WebRequest(getUrl("/api/admin/dbo/all")));
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
            JsonObject json = parseContentJsonObject(response);
            assertTrue(json.get("success").getAsBoolean());
            assertTrue(json.get("data").isJsonArray());
        }
    }

    @Test
    void testDao() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_ADMIN_EMAIL, TestDataConstants.JUNIT_ADMIN_PASSWORD)) {
            JsonObject json = parseContentJsonObject(
                    webClient.loadWebResponse(new WebRequest(getUrl(
                            "/api/admin/dbo/LoginUserDbo/"+TestDataConstants.JUNIT_USER_LOGINID
                    )))
            );
            assertTrue(json.get("success").getAsBoolean());
            assertTrue(json.get("data").isJsonObject());
            JsonObject jdao = json.get("data").getAsJsonObject();
            assertEquals(TestDataConstants.JUNIT_USER_LOGINID, jdao.get("id").getAsLong());
            assertTrue(jdao.get("active").getAsBoolean());
        }
    }
}
