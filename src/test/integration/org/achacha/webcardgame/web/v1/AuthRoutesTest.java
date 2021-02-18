package org.achacha.webcardgame.web.v1;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import jakarta.ws.rs.core.Response;
import org.achacha.test.BaseIntegrationTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthRoutesTest extends BaseIntegrationTest {
    private final URL ROUTE_URL = getUrl("/api/auth/login");

    @Test
    void testInvalidLogin() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            // HttpMethod here needs to be gargoylesoftware one otherwise apache one is a String and becomes accept method
            final WebRequest webRequest = new WebRequest(ROUTE_URL, HttpMethod.POST);

            // POST request parameters
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new NameValuePair("email", "invalid_user@invalid_domain"));
            pairs.add(new NameValuePair("pwd", ""));
            webRequest.setRequestParameters(pairs);

            // Submit POST
            final WebResponse response = webClient.loadWebResponse(webRequest);

            // Verify
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatusCode());
            JsonObject jobj = parseContentJsonObject(response);
            assertFalse(Preconditions.checkNotNull(jobj.get("success")).getAsBoolean());
        }
    }

    @Test
    void testUserLoginLogout() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_USER_EMAIL, TestDataConstants.JUNIT_USER_PASSWORD)) {
            //
            // Login
            //
            final WebRequest webRequest = new WebRequest(ROUTE_URL);
            final WebResponse response = webClient.loadWebResponse(webRequest);

            JsonObject json = parseContentJsonObject(response);
            assertTrue(json.get("success").getAsBoolean());
            JsonObject jUser = json.get("user").getAsJsonObject();
            assertEquals(TestDataConstants.JUNIT_USER_LOGINID, jUser.get("id").getAsLong());
            assertEquals(TestDataConstants.JUNIT_USER_EMAIL, jUser.get("email").getAsString());
            assertFalse(jUser.has("pwd"));
            assertFalse(jUser.has("salt"));
            assertFalse(jUser.get("superuser").getAsBoolean());

            //
            // Logout and verify
            //
            doLogoutAndVerify(webClient);
        }
    }

    @Test
    void testAdminLogin() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_ADMIN_EMAIL, TestDataConstants.JUNIT_ADMIN_PASSWORD)) {
            final WebRequest webRequest = new WebRequest(ROUTE_URL);
            final WebResponse response = webClient.loadWebResponse(webRequest);

            JsonObject json = parseContentJsonObject(response);
            assertTrue(json.get("success").getAsBoolean());
            JsonObject jAdmin = json.get("user").getAsJsonObject();
            assertEquals(TestDataConstants.JUNIT_ADMIN_LOGINID, jAdmin.get("id").getAsLong());
            assertEquals(TestDataConstants.JUNIT_ADMIN_EMAIL, jAdmin.get("email").getAsString());
            assertFalse(jAdmin.has("pwd"));
            assertFalse(jAdmin.has("salt"));
            assertFalse(jAdmin.get("superuser").getAsBoolean());
        }
    }

    @Test
    void testPiggyback() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_SU_EMAIL, TestDataConstants.JUNIT_SU_PASSWORD)) {
            //
            // Verify logged in with su
            //
            final WebResponse responseSu = webClient.loadWebResponse(new WebRequest(ROUTE_URL));
            JsonObject jsonSu = parseContentJsonObject(responseSu);
            assertTrue(jsonSu.get("success").getAsBoolean());
            JsonObject jSu = jsonSu.get("user").getAsJsonObject();
            assertEquals(TestDataConstants.JUNIT_SU_LOGINID, jSu.get("id").getAsLong());
            assertEquals(TestDataConstants.JUNIT_SU_EMAIL, jSu.get("email").getAsString());
            assertFalse(jSu.has("pwd"));
            assertFalse(jSu.has("salt"));
            assertTrue(jSu.get("superuser").getAsBoolean());

            //
            // Impersonate user via PUT
            //
            final WebRequest webRequestUser = new WebRequest(UrlUtils.getUrlWithNewQuery(ROUTE_URL, "email="+TestDataConstants.JUNIT_USER_EMAIL), HttpMethod.PUT);
            JsonObject jsonUser = parseContentJsonObject(webClient.loadWebResponse(webRequestUser));
            assertTrue(jsonUser.get("success").getAsBoolean());


            //
            // Verify user with GET
            //
            JsonObject jsonPiggy = parseContentJsonObject(webClient.loadWebResponse(new WebRequest(ROUTE_URL)));
            assertTrue(jsonPiggy.get("success").getAsBoolean());
            JsonObject jPiggy = jsonPiggy.get("user").getAsJsonObject();
            assertEquals(TestDataConstants.JUNIT_USER_EMAIL, jPiggy.get("email").getAsString());
            assertFalse(jPiggy.get("superuser").getAsBoolean());

            //
            // Logout and verify
            //
            doLogoutAndVerify(webClient);
        }
    }

    private void doLogoutAndVerify(WebClient webClient) throws IOException {
        //
        // Logout
        //
        final WebRequest webRequestDelete = new WebRequest(ROUTE_URL, HttpMethod.DELETE);
        final WebResponse responseDelete = webClient.loadWebResponse(webRequestDelete);
        JsonObject jsonDelete = parseContentJsonObject(responseDelete);
        assertTrue(jsonDelete.get("success").getAsBoolean());

        //
        // Verify logged out
        //
        final WebResponse responseVerify = webClient.loadWebResponse(new WebRequest(ROUTE_URL));
        JsonObject jsonVerify = parseContentJsonObject(responseVerify);
        assertTrue(jsonVerify.get("success").getAsBoolean());
        assertFalse(jsonVerify.has("user"));
    }
}
