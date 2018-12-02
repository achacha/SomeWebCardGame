package org.achacha.test;

/**
 * Well known constants populated into the database
 */
public class TestDataConstants {
    // login
    public static final long JUNIT_USER_LOGINID = 2;
    public static final String JUNIT_USER_EMAIL = "junit";
    public static final String JUNIT_USER_PASSWORD = "test";
    public static final long JUNIT_ADMIN_LOGINID = 3;
    public static final String JUNIT_ADMIN_EMAIL = "junitadmin";
    public static final String JUNIT_ADMIN_PASSWORD = "test";
    public static final long JUNIT_SU_LOGINID = 4;
    public static final String JUNIT_SU_EMAIL = "junitsu";
    public static final String JUNIT_SU_PASSWORD = "test";

    // player for JUNIT_USER_LOGINID
    public static final long JUNIT_PLAYER__ID = 1;
    public static final long JUNIT_INVENTORY_ID = 1;  // TODO: This will go away, data is to be generated dynamically and looked up by player id
}
