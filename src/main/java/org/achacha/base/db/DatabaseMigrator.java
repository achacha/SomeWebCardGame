package org.achacha.base.db;

import org.achacha.base.db.provider.JdbcDatabaseConnectionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;

/**
 * Database migration
 */
public class DatabaseMigrator {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseMigrator.class);

    public static void migrateTestDatabase(JdbcDatabaseConnectionProvider databaseConnectionProvider) {
        Flyway flyway = Flyway.configure()
                .dataSource(databaseConnectionProvider.getDataSource())
                .locations("filesystem:db/migration")
                .load();

        LOGGER.info("Migrating: {}", databaseConnectionProvider.toString());

        // Migrate
        flyway.clean();
        flyway.migrate();
    }
}
