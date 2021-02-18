package org.achacha.base.global;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletContextEvent;
import org.achacha.base.json.JsonEmittable;
import org.achacha.base.json.JsonHelper;

import javax.annotation.Nonnull;
import java.util.Properties;

/**
 * NOTE: Following convention in naming is used:
 *   URI = protocol://hostname:port/path    (never trailing /)
 *   URL = /path/to/resource.ext            (always leading /)
 */
public class GlobalProperties extends Properties implements JsonEmittable {
    /** Relative URI for admin sub-site */
    public static final String URI_ADMIN = "/admin";

    /** Relative URI for static directory */
    public static final String URI_STATIC = "/static";
    public static final String URI_JS = "/js";

    /** Base URI for API directory */
    public static final String URI_API_BASE = "/api";

    private static final String KEY_URI_HOME = "uri.home";
    private static final String KEY_URI_LOGIN_HOME = "uri.login.home";
    private static final String KEY_URI_LOGIN_TARGET = "uri.login.target";
    private static final String KEY_URL_HOME_PUBLIC = "url.home.public";

    /**
     * WebContext path with leading /
     * We default to /junit and if this is not a test it will be replaced with
     * a context path from the container
     */
    private String webContextPath = "/junit";

    /**
     * Extract values from ServletContext during startup
     * @param servletContextEvent ServletContextEvent or null if unit testing
     */
    protected void processServletContextEvent(ServletContextEvent servletContextEvent) {
        if (null != servletContextEvent)
            webContextPath = servletContextEvent.getServletContext().getContextPath();
    }

    /**
     * Example: /
     * @return path to URI where home is (leading / included)
     */
    public String getUriHome() {
        return getProperty(KEY_URI_HOME);
    }

    /**
     * Example: /index.jsp
     * @return path to URI where public login page is (leading / included)
     */
    public String getUriHomeLogin() {
        return getProperty(KEY_URI_LOGIN_HOME);
    }

    /**
     * Example: /index.jsp
     * @return path to URI where public login page is (leading / included)
     */
    public String getUriLoginTarget() {
        return getProperty(KEY_URI_LOGIN_TARGET);
    }

    /**
     * Example: https://my.host.com
     * @return String absolute path to URL where public home is (trailing / NOT included)
     */
    public String getUrlPublicHome() {
        return getProperty(KEY_URL_HOME_PUBLIC);
    }

    /**
     * Example /mycontext
     * @return String name of the web context (includes leading /)
     */
    @Nonnull
    public String getWebContextPath() {
        return webContextPath;
    }

    /**
     * Example: http://my.host.com/webcontext
     * @return String absolute path to public home and web context (trailing / NOT included)
     */
    public String getUrlPublicContextBase() {
        return getProperty(KEY_URL_HOME_PUBLIC) + webContextPath;
    }

    @Override
    public JsonObject toJsonObjectAdmin() {
        return toJsonObject();
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jobj = JsonHelper.toJsonObject(this);
        jobj.addProperty("webContextPath", webContextPath);
        return jobj;
    }
}
