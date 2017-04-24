package org.achacha.base.context;

import com.google.common.net.MediaType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.achacha.base.dbo.EventLogDboFactory;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.dbo.LoginUserDboFactory;
import org.achacha.base.global.Global;
import org.achacha.base.i18n.UIMessageHelper;
import org.achacha.base.json.JsonEmittable;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.logging.Event;
import org.achacha.base.web.ServletHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
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
     * Parse POST body that contains JsonObject
     * @return JsonObject parsed from body
     * @throws RuntimeException if can't parse or content type mismatch
     */
    @Nonnull
    public JsonObject getJsonObjectFromRequestBody() {
        return parsePostBodyIntoJson().getAsJsonObject();
    }

    /**
     * Parse POST body that contains JsonObject
     * @return JsonObject parsed from body
     * @throws RuntimeException if can't parse or content type mismatch
     */
    @Nonnull
    public JsonArray getJsonArrayFromRequestBody() {
        return parsePostBodyIntoJson().getAsJsonArray();
    }

    /**
     * Parse HTTP request body in JsonElement
     * @return JsonElement
     */
    private JsonElement parsePostBodyIntoJson() {
        if (requestJsonElement == null) {
            if (!isRequestContentType(MediaType.JSON_UTF_8))
                throw new RuntimeException("Expected: application/json");

            try {
                requestJsonElement = JsonHelper.fromInputStream(request.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse HTTP input stream", e);
            }
        }

        return requestJsonElement;
    }

    /**
     * Set current user logged in, save to session
     * @param loginDbo LoginUserDbo
     */
    public void setLogin(LoginUserDbo loginDbo) {
        if (null == loginDbo) {
            LOGGER.error("Set login called with a null object");
        }
        else {
            login = loginDbo;
            request.getSession().setAttribute(SESSION_LOGIN_PARAM, loginDbo);

            // Update timestamp for last_login_on
            login.saveLastLoginOnNow();
        }
    }

    /**
     * Perform a login by checking the database and if valid adding this login user to session
     * @param username String dto LoginAttamptDto with all fields present
     * @return true if login is valid
     */
    public LoginResult login(String username, String pwd) {
        // Try to login the user
        LoginUserDbo login = LoginUserDboFactory.login(username, pwd);
        if (null != login) {
            // Login success
            setLogin(login);
            EventLogDboFactory.insertFromContex(Event.LOGIN, login.toJsonObject());
            return LoginResult.SUCCESS;
        }
        else {
            // Login failed, log event
            JsonObject obj = new JsonObject();
            obj.addProperty("usename", username);
            EventLogDboFactory.insertInternal(Event.LOGIN_FAIL, obj);
        }

        // Default is always false, return true must be a result of a valid login
        return LoginResult.FAILURE;
    }

    /**
     * Impersonate a login
     * @return true is successfully impersonated
     */
    public Event impersonate(String email) {
        if (login.isSuperuser()) {
            LoginUserDbo impersonated  = LoginUserDboFactory.impersonate(email);
            if (null != impersonated) {
                JsonObject data = new JsonObject();
                data.addProperty("impersonated", impersonated.getId());
                data.addProperty("impersonator", login.getId());
                EventLogDboFactory.insertFromContex(Event.LOGIN_IMPERSONATE, data);

                impersonated.setImpersonator(login);
                setLogin(impersonated);
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
     * Create JSON object as response
     * Status is 200 - HttpServletResponse.SC_OK
     *
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnSuccess() {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getSuccessObject();

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Create JSON object as response
     * Attach message and data elements if not null
     * Status is 200 - HttpServletResponse.SC_OK
     *
     * @param message String
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnSuccess(String message) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getSuccessObject();

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        if (null != message)
            obj.addProperty(JsonHelper.MESSAGE, message);

        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Create JSON object as response
     * Attach message and data elements if not null
     * Status is 200 - HttpServletResponse.SC_OK
     *
     * @param message String
     * @param data JsonElement to be added inside return object as 'data'
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnSuccess(String message, JsonElement data) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getSuccessObject();

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        if (null != message)
            obj.addProperty(JsonHelper.MESSAGE, message);

        if (null != data)
            obj.add(JsonHelper.DATA, data);

        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Create JSON object as response
     * Attach message and data elements if not null
     * Status is 200 - HttpServletResponse.SC_OK
     *
     * @param message String
     * @param data JsonEmittable to be added inside return object as 'data'
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnSuccess(String message, JsonEmittable data) {
        return returnSuccess(message, data.toJsonObject());
    }

    /**
     * Create JSON object as response
     * Status is 200 - HttpServletResponse.SC_OK
     *
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnSuccess(JsonObject obj) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Default response for methods not implemented
     * Status is 501 - HttpServletResponse.SC_NOT_IMPLEMENTED
     *
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnNotImplemented() {
        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        LOGGER.info("Returning not implemented for URI={}", request.getRequestURI());

        JsonObject obj = JsonHelper.getFailObject(UIMessageHelper.getInstance().getLocalizedMsg("general.notimplemented"));
        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }
        return obj;
    }

    /**
     * Return exception in error message
     * Status is 500 - HttpServletResponse.SC_INTERNAL_SERVER_ERROR
     * FailObject returned - { success:false, ... }
     * Message is from the exception
     *
     * @param t if not null expanded and added
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnError(Throwable t) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getFailObject(t.getLocalizedMessage());
        JsonHelper.putException(obj, t);

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        LOGGER.error("Returning error, object=" + obj.toString() + " for " + this, t);
        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Returns message about unsupported media type
     * Status is 415 - HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE
     *
     * @param expected MediaType
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnInvalidContentType(MediaType expected) {
        response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getFailObject(UIMessageHelper.getInstance().getLocalizedMsg("error.invalid.content.type", expected));
        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        LOGGER.warn("Returning unsupported media type={}, expected={}", request.getContentType(), expected);
        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Return exception in error message, do not log as error (warn instead)
     * Status is 500 - HttpServletResponse.SC_INTERNAL_SERVER_ERROR
     * FailObject returned - { success:false, ... }
     * Message is from the exception
     *
     * @param t if not null expanded and added
     * @return JsonObject written to response
     */
    @Nonnull
    public JsonObject returnErrorLogAsWarn(Throwable t) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getFailObject(t.getLocalizedMessage());
        if (null != t) { JsonHelper.putException(obj, t); }

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        LOGGER.warn("Returning error, object=" + obj.toString() + " for " + this, t);
        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send error object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * @return Uri relative to web context (with leading /)
     */
    @Nonnull
    public static String getUriRelativeToWebContext(HttpServletRequest request) {
        String contextPath = Global.getInstance().getProperties().getWebContextPath();
        LOGGER.debug("Resolving relative to web context URL: {} for context: {}", request.getRequestURI(), contextPath);
        String requestUri = request.getRequestURI();
        if (null != requestUri)
            return requestUri.substring(contextPath.length());
        else {
            LOGGER.error("Null RequestURI detected: "+ ServletHelper.toHtml(request));
            return "";
        }
    }

    /**
     * Return non-localize fail message
     * @param message String actual message
     * @param data String
     * @return JsonObject
     */
    @Nonnull
    public JsonObject returnNonLocalizedFail(String message, String data) {
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getFailObject(message);
        obj.addProperty(JsonHelper.DATA, data);

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        LOGGER.warn("Returning error, object=" + obj.toString() + " for " + this+" message="+message+" data="+data);
        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send non-localized fail object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Return error JsonObject about missing parameter
     * @param paramName String
     * @return JsonObject
     */
    @Nonnull
    public JsonObject returnMissingParameter(String paramName) {
        return returnFail("error.missing.param", paramName);
    }

    /**
     * Return error JsonObject about missing object
     * @param paramName String
     * @return JsonObject
     */
    @Nonnull
    public JsonObject returnMissingObject(String paramName) {
        response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
        return returnFail("error.missing.object", paramName);
    }

    /**
     * Return error JsonObject about missing parameter
     * @param paramName String
     * @return JsonObject
     */
    @Nonnull
    public JsonObject returnNotFound(String paramName) {
        return returnFail("error.not.found", paramName);
    }

    /**
     * Return localize fail message
     * @param key String key to localized message
     * @param data String
     * @return JsonObject
     */
    @Nonnull
    public JsonObject returnFail(String key, String data) {
        response.setContentType(MediaType.JSON_UTF_8.toString());

        JsonObject obj = JsonHelper.getFailObject(UIMessageHelper.getInstance().getLocalizedMsg(key, data));
        obj.addProperty(JsonHelper.DATA, data);

        obj.addProperty(JsonHelper.AUTHORIZED, null != login);

        LOGGER.warn("Returning localized fail object=" + obj.toString() + " for " + this+" key="+key+" data="+data);
        try {
            response.getWriter().println(obj.toString());
        } catch (IOException e) {
            LOGGER.error("Failed to send fail object="+obj+" for "+this, e);
        }

        return obj;
    }

    /**
     * Makes sure request parameter is provided and not blank
     * @param param String
     * @return true if exists and not blank
     */
    public boolean isParamBlankOrMissing(String param) {
        return StringUtils.isBlank(request.getParameter(param));
    }

    /**
     * Parse body for JSON and save it in this context
     * Does not validate content-type
     * @return JsonElement or null
     * @see #isRequestContentType(MediaType)
     */
    public JsonElement fromBody() {
        if (requestJsonElement == null) {
            try {
                requestJsonElement = JsonHelper.fromInputStream(request.getInputStream());
            } catch (IOException e) {
                LOGGER.error("Failed to parse body for JSON", e);
            }
        }
        return requestJsonElement;
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
}
