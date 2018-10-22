package org.achacha.base.security;

import org.achacha.base.dbo.LoginUserDbo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class SecurityHelper {
    private static final Logger LOGGER = LogManager.getLogger(SecurityHelper.class);

    private static final int SALT_SIZE = 64;

    /**
     * Salt password
     * @param password String
     * @param salt String
     * @return SHA-256 salted password hash
     */
    public static String encodeSaltPassword(@Nonnull String password, @Nonnull String salt) {
        return DigestUtils.sha256Hex(""+password+salt);
    }

    /**
     * @return Random string used for salt
     */
    public static String generateSalt() {
        return RandomStringUtils.randomAlphanumeric(SALT_SIZE);
    }

    /**
     * Set password on login
     * @param login LoginUserDao
     * @param newPwd String new raw password
     */
    public static void savePassword(LoginUserDbo login, String newPwd) {
        String salt = SecurityHelper.generateSalt();
        String saltedPwd = SecurityHelper.encodeSaltPassword(newPwd, salt);
        login.setPwd(saltedPwd);
        login.setSalt(salt);
        try {
            login.save();
        }
        catch(Exception e) {
            LOGGER.error("Failed update password for login="+login, e);
        }
        LOGGER.debug("Password updated for login={} salt={} saltedPwd=", login, salt, saltedPwd);
    }
}
