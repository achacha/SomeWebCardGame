package org.achacha.base.global;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.UnitTestDatabaseMigrator;
import org.achacha.base.db.provider.ResourceSqlProvider;
import org.achacha.base.db.provider.UnitTestDbPoolConnectionProvider;

import java.util.Properties;

/**
 * Testing specific global instance
 * This is also used in the embedded tomcat for integration testing
 * @see org.achacha.base.web.ServerGlobalInit
 */
public class GlobalForTest extends Global {
    // To be found in user's home directory
    private static final String DEFAULT_PROPERTIES_FILE = ".sawcog.properties";

    // Keep track of database migration
    private static boolean isDatabaseMigrated = true;

    public GlobalForTest() {
        super("TEST", DEFAULT_PROPERTIES_FILE);
    }

    @Override
    public void initChild() {
        // Simulate production when testing
        this.mode = Mode.PRODUCTION;
    }

    @Override
    public void initDatabaseManager() {
        LOGGER.info("ROOT: Creating database manager");
        synchronized(this) {
            if (databaseManager == null) {
                final Properties dbProperties = getDbProperties();

                databaseManager = new DatabaseManager(
                        new UnitTestDbPoolConnectionProvider(dbProperties),
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

    @Override
    public Properties getDbProperties() {
        final Properties dbProperties = new Properties();
        final String dbPrefix = "flyway.test.db.";


        // Properties are found in the $HOME of user and prefixed with 'db.'
        // Copy all properties that start with 'db.' into separate properties file to be used in DB init
        properties.keySet().stream()
                .map(Object::toString)
                .filter(key -> key.startsWith(dbPrefix))
                .forEach(key -> dbProperties.setProperty(
                        key.substring(dbPrefix.length()),
                        Global.getInstance().getProperties().getProperty(key)
                        )
                );
        LOGGER.debug("Migrator dbProperties={}", dbProperties);
        return dbProperties;
    }

}
