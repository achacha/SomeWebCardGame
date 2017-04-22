package org.achacha.base.global;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.UnitTestDatabaseMigrator;
import org.achacha.base.db.provider.ResourceSqlProvider;
import org.achacha.base.db.provider.UnitTestDbPoolConnectionProvider;

import java.util.Properties;

/**
 * Testing specific global instance
 */
public class UnitTestGlobal extends Global {
    // To be found in user's home directory
    private static final String DEFAULT_PROPERTIES_FILE = ".sawcog.TEST.properties";

    public static final long JUNIT_LOGINID = 2;
    public static final String JUNIT_EMAIL = "junit";
    public static final String JUNIT_PASSWORD = "test";
    public static final long JUNITADMIN_LOGINID = 3;
    public static final String JUNITADMIN_EMAIL = "junitadmin";

    public UnitTestGlobal() {
        super("TEST", DEFAULT_PROPERTIES_FILE);
    }

    @Override
    public void initChild() {

    }

    @Override
    public void initDatabaseManager() {
        String jdbcUrl = properties.getProperty("db.jdbc.url");
        Properties dbProperties = new Properties();
        dbProperties.setProperty("jdbcUrl", jdbcUrl);
        dbProperties.setProperty("username", properties.getProperty("db.user"));
        dbProperties.setProperty("password", properties.getProperty("db.password"));
        databaseManager = new DatabaseManager(
                new UnitTestDbPoolConnectionProvider(jdbcUrl, dbProperties),
                new ResourceSqlProvider()
        );

        // Perform migrations
        UnitTestDatabaseMigrator.migrateTestDatabase();
    }
}
