package org.achacha.base.context;

import com.google.common.net.MediaType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.achacha.base.dbo.EventLogDboFactory;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.dbo.LoginUserDboFactory;
import org.achacha.base.global.Global;
import org.achacha.base.logging.Event;
import org.achacha.base.security.SecurityHelper;
import org.achacha.webcardgame.helper.ResponseHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Optional;

public class CallContext {
    private static final Logger LOGGER = LogManager.getLogger(CallContext.class);

    public static final String DEFAULT_ENCODING = "UTF-8";

    // Used by login to redirect to requesting page
    public static final String SESSION_REDIRECT_FROM = "redirectToLoginFrom";

    // /favicon.ico
    public static final String CONTENT_TYPE_IMAGE_FAVICON = "image/x-icon";
    public static final String FAVICON_ICO = "/favicon.ico";

    // /robots.txt
    public static final String ROBOTS_TXT = "/robots.txt";

    public static final String SESSION_LOGIN_PARAM = "login";

    public enum LoginResult { SUCCESS, FAILURE, LOCKOUT }

    // Creation time of this Context
    private long createdTimeMillis = System.currentTimeMillis();

    /**
     * Servlet request
     */
    protected HttpServletRequest request;

    /**
     * JsonElement from request body (can be either JsonObject or JsonArray)
     */
    private JsonElement requestJsonElement;

    /**
     * Servlet response
     */
    private HttpServletResponse response;

    /**
     * Method from the REST call
     */
    protected String method;

    /** Currently logged in user, null if none */
    protected LoginUserDbo login;

    /**
     * Construct a call context
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param method String method
     */
    public CallContext(HttpServletRequest request, HttpServletResponse response, String method) {
        if (request == null || response == null)
            throw new RuntimeException("Context cannot be created with null request nor response for filter chain");

        this.request = request;
        this.response = response;
        this.method = method;

        login = fromSession();
    }

    /**
     * Construct a call context
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    public CallContext(HttpServletRequest request, HttpServletResponse response) {
        if (request == null || response == null)
            throw new RuntimeException("Context cannot be created with null request nor response for filter chain");

        this.request = request;
        this.response = response;

        login = fromSession();
    }

    /**
     * @return HttpServletRequest
     */
    @Nonnull
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * You can also call getRequest().getSession() but that will create a session if there isn't one which may not be needed in some cases
     * @return Optional HttpSession, since it may not yet exist if the use is about to log in or has made anonymous calls
     */
    public Optional<HttpSession> getSession() {
        return Optional.ofNullable(request.getSession(false));
    }

    /**
     * @return HttpServletResponse
     */
    @Nonnull
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * @return METHOD specified for this REST call
     */
    @Nonnull
    public String getMethod() {
        return method;
    }

    /**
     * @return LoginUserDbo currently logged in user
     */
    public LoginUserDbo getLogin() {
        return login;
    }

    /**
     * Find user in session
     * @return LoginUserDbo or null if user not found or session DNE
     */
    public LoginUserDbo fromSession() {
        return fromSession(request.getSession());
    }

    /**
     * Find user in provided session
     * @param session HttpSession
     * @return LoginUserDbo or null if user not found or session DNE
     */
    public static LoginUserDbo fromSession(HttpSession session) {
        LoginUserDbo login = null;
        if (null != session) {
            login = (LoginUserDbo) session.getAttribute(SESSION_LOGIN_PARAM);
        }

        return login;
    }

    /**
     * Set current user logged in, save to session
     * @param login LoginUserDbo
     */
    public void setLogin(LoginUserDbo login) {
        if (null == login) {
            // To logout a user call logout() which handles session invalidation
            LOGGER.error("Set login called with a null object");
        }
        else {
            this.login = login;
            request.getSession().setAttribute(SESSION_LOGIN_PARAM, login);

            // Update timestamp for last_login_on
            login.touch();
        }
    }

    /**
     * Perform a login by checking the database and if valid adding this login user to session
     * @param email String dto LoginAttamptDto with all fields present
     * @param pwd String password
     * @return LoginResult
     */
    public LoginResult login(String email, String pwd) {
        if (StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(pwd)) {
            LoginUserDbo attemptLogin = LoginUserDboFactory.findByEmail(email);
            if (attemptLogin != null) {
                String hashPassword = SecurityHelper.encodeSaltPassword(pwd, attemptLogin.getSalt());
                if (Global.getInstance().isDevelopment()) {
                    // This should only be visible in development mode
                    LOGGER.debug("Login attempt email={} pwd={} hash={}", email, pwd, hashPassword);
                }
                else {
                    LOGGER.debug("Login attempt email={} pwd=****** hash={}", email, hashPassword);
                }
                if (hashPassword.equals(attemptLogin.getPwd())) {
                    // Login success
                    setLogin(attemptLogin);
                    EventLogDboFactory.insertFromContex(Event.LOGIN, login.toJsonObject());
                    return LoginResult.SUCCESS;
                }
            }

            // Login failed, log event
            JsonObject obj = new JsonObject();
            obj.addProperty("email", email);
            obj.addProperty("pwd", pwd);
            EventLogDboFactory.insertInternal(Event.LOGIN_FAIL, obj);

            // Remove any existing login
            Optional<HttpSession> optSession = CallContextTls.get().getSession();
            optSession.ifPresent(session -> session.removeAttribute(CallContext.SESSION_LOGIN_PARAM));
        }

        // TODO: Keep track of failed logins? Or do manage it based on caller IP?

        // Default is always false, return true must be a result of a valid login
        return LoginResult.FAILURE;
    }

    /**
     * Impersonate a login
     * @param email String
     * @return Event of impersonation
     */
    public Event impersonate(String email) {
        if (login.isSuperuser()) {
            LoginUserDbo impersonated  = LoginUserDboFactory.impersonate(email);
            if (null != impersonated) {
                JsonObject data = new JsonObject();
                data.addProperty("impersonated", impersonated.getId());
                data.addProperty("impersonator", login.getId());
                EventLogDboFactory.insertFromContex(Event.LOGIN_IMPERSONATE, data);

                return Event.LOGIN_IMPERSONATE;
            }
            else {
                LOGGER.error("Failed to find user with email="+email);
                return Event.LOGIN_FAIL;
            }
        }
        else {
            LOGGER.error("Impersonation is only allowed by superuser, login={}", login);
            return Event.LOGIN_PERMISSION_INVALID;
        }
    }

    /**
     * Logout currently logged in user
     * Invalidate HTTP session
     */
    public void logout() {
        HttpSession session = request.getSession();
        if (null != session) {
            EventLogDboFactory.insertFromContex(Event.LOGOUT);

            // Invalidate session
            session.invalidate();
        }

        // Clear out the login
        this.login = null;
    }

    /**
     * Time in millis when Context object was created
     * @return long millis time
     */
    public long getCreatedTimeMillis() {
        return createdTimeMillis;
    }

    public Instant getCreatedTime() {
        return Instant.ofEpochMilli(createdTimeMillis);
    }

    /**
     * @return Base URL of the request
     */
    public String getBaseUrl() {
        return request.getRequestURL().toString().replace(request.getRequestURI(), "");
    }

    /**
     * @param request HttpServletRequest
     * @return Uri relative to web context (with leading /)
     */
    @Nonnull
    public static String getUriRelativeToWebContext(HttpServletRequest request) {
        String contextPath = Global.getInstance().getProperties().getWebContextPath();
        LOGGER.debug("Resolving relative to web context URI={} for contextPath={}", request.getRequestURI(), contextPath);
        String requestUri = request.getRequestURI();
        if (null != requestUri)
            return requestUri.substring(contextPath.length());
        else {
            LOGGER.error("Null RequestURI detected: "+ ResponseHelper.toHtml(request));
            return "";
        }
    }

    /**
     * Checks HTTP request content-type
     * @param expected MediaType
     * @return true if is that media type
     */
    public boolean isRequestContentType(MediaType expected) {
        MediaType mt = MediaType.parse(request.getContentType());
        return mt.type().equals(expected.type()) && mt.subtype().equals(expected.subtype());
    }

    /**
     * @return true is valid user is logged in and is superuser
     */
    public boolean isLoggedInAsSuperuser() {
        return (login != null && login.isSuperuser());
    }
}
