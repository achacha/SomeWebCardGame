package org.achacha.base.security;

/**
 * Security level
 */
public enum SecurityLevel {
    PUBLIC(0),
    AUTHENTICATED(100),
    ADMIN(255);

    private final int level;

    SecurityLevel(int level) {
        this.level = level;
    }

    public int level() { return level; }

    /**
     * Get enum from level
     * @param level int
     * @return SecurityLevel
     */
    public static SecurityLevel valueOf(int level) {
        if (level == PUBLIC.level) return PUBLIC;
        else if (level == AUTHENTICATED.level) return AUTHENTICATED;
        else if (level == ADMIN.level) return ADMIN;
        else
            throw new SecurityException("Invalid security level="+level);
    }

    /**
     * Check if this Security level is high enough and is allowed access
     * @param minimumRequiredLevel minimum level required
     * @return true if security level is high enough and access is allowed
     */
    public boolean isLevelSufficient(SecurityLevel minimumRequiredLevel) {
        return this.level >= minimumRequiredLevel.level;
    }

    /**
     * Check if API entry point is public and doesn't require authentication
     * @return true if public API
     */
    public boolean isPublic() { return this.level == PUBLIC.level; }
}
