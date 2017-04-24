package org.achacha.webcardgame.helper;

import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.dbo.LoginUserDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginHelper {
    protected static final Logger LOGGER = LogManager.getLogger(LoginHelper.class);

    public static boolean isAbleToLogIn(String email, String hashPassword) {
        LoginUserDbo user = LoginUserDboFactory.findByEmail(email);
        if (user != null && hashPassword.equals(user.getPwd())) {
            CallContextTls.get().setLogin(user);
            LOGGER.debug("Login success: email={} hashPassword={}", email, hashPassword);
            return true;
        }
        else {
            LOGGER.debug("Login fail: email={} hashPassword={}", email, hashPassword);
            CallContextTls.get().getSession().ifPresent(session -> session.removeAttribute(CallContext.SESSION_LOGIN_PARAM));
            return false;
        }
    }
}
