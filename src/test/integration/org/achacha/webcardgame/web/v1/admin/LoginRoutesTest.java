package org.achacha.oddity.web.v1.admin;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import jakarta.ws.rs.core.Response;
import org.achacha.test.BaseIntegrationTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRoutesTest extends BaseIntegrationTest {
    @Test
    void testLoginWithNonSuAndFailToGetLogin() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_ADMIN_EMAIL, TestDataConstants.JUNIT_ADMIN_PASSWORD)) {
            final WebResponse response = webClient.loadWebResponse(new WebRequest(getUrl("/api/admin/login/"+TestDataConstants.JUNIT_SU_LOGINID)));

            // Only superuser should be allowed
            assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatusCode());
        }
    }

    @Test
    void testLoginWithSuAndGetLogin() throws IOException {
        try (final WebClient webClient = getWebClientWithLogin(TestDataConstants.JUNIT_SU_EMAIL, TestDataConstants.JUNIT_SU_PASSWORD)) {
            final WebResponse response = webClient.loadWebResponse(new WebRequest(getUrl("/api/admin/login/"+TestDataConstants.JUNIT_SU_LOGINID)));

            // Only superuser should be allowed
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        }
    }
}
