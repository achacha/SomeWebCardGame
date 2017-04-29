package org.achacha.webcardgame.helper;

import com.google.common.net.HttpHeaders;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.dbo.LoginUserDboFactory;
import org.achacha.base.global.Global;
import org.achacha.base.global.GlobalProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class LoginHelper {
    protected static final Logger LOGGER = LogManager.getLogger(LoginHelper.class);

    /**
     * @param uri URI
     * @return true if the URI does not need authentication and is part of static content (such as JS includes, images, etc)
     */
    public static boolean isPublicUri(String uri) {
        return  isStaticUri(uri)
                || uri.startsWith(Global.getInstance().getProperties().getUriHomeLogin())
                || isLoginTargetUri(uri)
        ;
    }

    /**
     * @param uri URI
     * @return true if this is a static content Uri
     */
    public static boolean isStaticUri(String uri) {
        return     uri.startsWith(GlobalProperties.URI_STATIC)
                || uri.startsWith(GlobalProperties.URI_JS);
    }

    /**
     * @param uri URI
     * @return true if this is a call to login target
     */
    public static boolean isLoginTargetUri(String uri) {
        return uri.startsWith(Global.getInstance().getProperties().getUriLoginTarget());
    }

    public static boolean isAbleToLogIn(String email, String hashPassword) {
        LoginUserDbo user = LoginUserDboFactory.findByEmail(email);
        if (user != null && hashPassword.equals(user.getPwd())) {
            CallContextTls.get().setLogin(user);
            LOGGER.debug("Login success: email={} hashPassword={}", email, hashPassword);
            return true;
        }
        else {
            LOGGER.debug("Login fail: email={} hashPassword={}", email, hashPassword);
            Optional<HttpSession> optSession = CallContextTls.get().getSession();
            optSession.ifPresent(session -> session.removeAttribute(CallContext.SESSION_LOGIN_PARAM));
            return false;
        }
    }

    /**
     * Get originating URI and set it on session so we can redirect to it
     * @param request HttpServletRequest
     * @return URI string
     */
    public static void processOriginatingUri(HttpServletRequest request) {
        String baseOriginatingUrl = request.getParameter(HttpHeaders.REFERER);
        if (baseOriginatingUrl == null && request.getSession() != null && request.getSession().getAttribute(CallContext.SESSION_REDIRECT_FROM) == null) {
            // We do not have a referer and we do not have a target to redirect to, use originating URI
            baseOriginatingUrl = request.getRequestURI();
        }
        if (baseOriginatingUrl != null) {
            // We have a URL to return to
            StringBuilder originatingUrl = new StringBuilder(baseOriginatingUrl);
            if (null != request.getQueryString()) {
                originatingUrl.append('?');
                originatingUrl.append(request.getQueryString());
            }
            request.getSession(true).setAttribute(CallContext.SESSION_REDIRECT_FROM, originatingUrl.toString());
        }
    }
}
