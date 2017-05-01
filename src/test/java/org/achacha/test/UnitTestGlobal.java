package org.achacha.test;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.UnitTestDatabaseMigrator;
import org.achacha.base.db.provider.ResourceSqlProvider;
import org.achacha.base.db.provider.UnitTestDbPoolConnectionProvider;
import org.achacha.base.global.Global;

import java.util.Properties;

/**
 * Testing specific global instance
 */
public class UnitTestGlobal extends Global {
    // To be found in user's home directory
    private static final String DEFAULT_PROPERTIES_FILE = ".sawcog.properties";

    // Keep track of database migration
    private static boolean isDatabaseMigrated = false;

    public UnitTestGlobal() {
        super("TEST", DEFAULT_PROPERTIES_FILE);
    }

    @Override
    public void initChild() {
        // Simulate production when testing
        this.mode = Mode.PRODUCTION;
    }

    @Override
    public void initDatabaseManager() {
        synchronized(this) {
            if (databaseManager == null) {
                final Properties dbProperties = getDbProperties("test.");

                String jdbcUrl = dbProperties.getProperty("jdbcUrl");
                databaseManager = new DatabaseManager(
                        new UnitTestDbPoolConnectionProvider(jdbcUrl, dbProperties),
                        new ResourceSqlProvider()
                );

                // Perform migrations
                if (!isDatabaseMigrated) {
                    UnitTestDatabaseMigrator.migrateTestDatabase();
                    isDatabaseMigrated = true;
                }
            }
        }
    }
}
