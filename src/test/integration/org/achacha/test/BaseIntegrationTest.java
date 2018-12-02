package org.achacha.test;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTest {
    private static final Logger LOGGER = LogManager.getLogger(BaseIntegrationTest.class);

    static final String HOSTNAME = "localhost";
    static final int PORT = 10081;

    private static JsonParser jsonParser = new JsonParser();

    /**
     * Return URL object base+suffix
     * @param relativeSuffix String URL must have leading /
     * @return URL object or base+suffix
     */
    @Nonnull
    protected static URL getUrl(String relativeSuffix) {
        String url = "http://" + HOSTNAME + ":" + PORT + relativeSuffix;
        try {
            LOGGER.debug("URL base resolved to: {}", url);
            return new URL(url);
        }
        catch(MalformedURLException e) {
            throw new RuntimeException("Malformed URL="+url, e);
        }
    }

    /**
     * Return URL object base+suffix?query
     * @param relativeSuffix String URL must have leading /
     * @return URL object or base+suffix?query
     */
    @Nullable
    protected static URL getUrl(String relativeSuffix, String queryString) {
        String url = "http://" + HOSTNAME + ":" + PORT + relativeSuffix + "?" + queryString;
        try {
            LOGGER.debug("URL base resolved to: {}", url);
            return new URL(url);
        }
        catch(MalformedURLException e) {
            LOGGER.error("Malformed URL: "+url);
        }
        return null;
    }

    /**
     * Parse JsonElement from String and log result to debug
     * @param webResponse WebResponse
     * @return JsonElement
     */
    public static JsonElement parseContentJson(WebResponse webResponse) {
        assertTrue(webResponse.getContentType().startsWith(MediaType.APPLICATION_JSON));

        JsonElement je = jsonParser.parse(webResponse.getContentAsString());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("JSON={}", je.toString());
        }
        return je;
    }

    /**
     * Parse String to JsonObject and validate
     * @param webResponse WebResponse
     * @return JsonObject
     */
    public static JsonObject parseContentJsonObject(WebResponse webResponse) {
        JsonElement je = parseContentJson(webResponse).getAsJsonObject();
        assertTrue(je.isJsonObject());
        return je.getAsJsonObject();
    }

    /**
     * Should be called before all test methods
     * Brings up separate thread of for tomcat
     */
    @BeforeAll
    public static void beforeClass() {
        System.out.println("beforeAll");

        BaseInitializedTest.init();

        if (!TomcatService.isTomcatRunning())
            TomcatService.startTomcat();
    }

    /**
     * Last thing called will stop tomcat
     */
    @AfterAll
    public static void afterClass() {
        if (!TomcatService.isTomcatRunning())
            TomcatService.stopTomcat();

        BaseInitializedTest.deinit();
    }

    /**
     * Create WebClient and login
     * WebClient is NOT closed
     * @param username String
     * @param password String
     * @return WebClient that needs to be closed when done (use try-with-resources)
     * @throws IOException on bad URL or network issue
     */
    protected WebClient getWebClientWithLogin(String username, String password) throws IOException {
        final WebClient webClient = new WebClient();

        // HttpMethod here needs to be gargoylesoftware one otherwise apache one is a String and becomes accept method
        WebRequest webRequest = new WebRequest(getUrl("/api/auth/login"), HttpMethod.POST);

        // POST request parameters
        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new NameValuePair("email", username));
        pairs.add(new NameValuePair("pwd", password));
        webRequest.setRequestParameters(pairs);

        // Submit POST
        final Page page1 = webClient.getPage(webRequest);
        WebResponse response = page1.getWebResponse();

        // Verify
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON));
        assertEquals(200, response.getStatusCode());
        JsonObject jobj = parseContentJsonObject(response);
        assertTrue(Preconditions.checkNotNull(jobj.get("success")).getAsBoolean());

        return webClient;
    }
}
