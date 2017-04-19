package org.achacha.base.security;

import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.Nonnull;

public class SecurityHelper {
    /**
     * Salt password
     * @param password String
     * @param salt String
     * @return SHA-256 salted password hash
     */
    public static String encodeSaltPassword(@Nonnull String password, @Nonnull String salt) {
        return DigestUtils.sha256Hex(""+password+salt);
    }
}
