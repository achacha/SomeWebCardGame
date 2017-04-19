package org.achacha.base.global;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.provider.ResourceSqlProvider;
import org.achacha.base.db.provider.TestConnectionProvider;

import java.util.Properties;

/**
 * Testing specific global instance
 */
public class UnitTestGlobal extends Global {
    public static final long JUNIT_LOGINID = 2;
    public static final String JUNIT_EMAIL = "junit";
    public static final String JUNIT_PASSWORD = "test";
    public static final long JUNITADMIN_LOGINID = 3;
    public static final String JUNITADMIN_EMAIL = "junitadmin";

    public UnitTestGlobal() {
        super("TEST", ".SomeWebCardGame.TEST.properties");
    }

    @Override
    public void initChild() {

    }

    @Override
    public void initDatabaseManager() {
        String jdbcUrl = properties.getProperty("db.jdbc.url");
        Properties dbProperties = new Properties();
        dbProperties.setProperty("user", properties.getProperty("db.jdbc.user"));
        dbProperties.setProperty("password", properties.getProperty("db.jdbc.password"));
        databaseManager = new DatabaseManager(
                new TestConnectionProvider(jdbcUrl, dbProperties),
                new ResourceSqlProvider()
        );
    }
}
