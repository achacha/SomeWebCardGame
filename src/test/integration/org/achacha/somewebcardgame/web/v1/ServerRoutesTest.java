package org.achacha.somewebcardgame.web.v1;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.google.gson.JsonObject;
import org.achacha.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerRoutesTest extends BaseIntegrationTest {
    private final URL ROUTE_URL = getUrl("/api/server/status");

    @Test
    void testGetStatus() throws IOException {
        try (final WebClient webClient = new WebClient()) {
            // Get page for URL
            final Page page1 = webClient.getPage(ROUTE_URL);
            WebResponse response = page1.getWebResponse();

            /*
            {"success":true,"httpRequest":"org.apache.catalina.connector.RequestFacade@797e75e","requestContext":"org.glassfish.jersey.server.ContainerRequest@690c3186","application":"org.glassfish.jersey.server.ResourceConfig$WrappingResourceConfig@4114b9ae","uriInfo":"org.glassfish.jersey.server.internal.routing.UriRoutingContext@59381e42","servletContext":"org.apache.catalina.core.ApplicationContextFacade@612daf2c"}
             */
            JsonObject json = parseContentJsonObject(response);
            assertTrue(json.get("success").getAsBoolean());
        }
    }
}
