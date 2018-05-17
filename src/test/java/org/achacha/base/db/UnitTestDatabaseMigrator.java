package org.achacha.base.db;

import org.achacha.base.db.provider.UnitTestDbPoolConnectionProvider;
import org.achacha.base.global.Global;
import org.flywaydb.core.Flyway;
import org.junit.Assert;

public class UnitTestDatabaseMigrator {

    public static void migrateTestDatabase() {
        Flyway flyway = new Flyway();
        UnitTestDbPoolConnectionProvider dbConnProvider = (UnitTestDbPoolConnectionProvider) Global.getInstance().getDatabaseManager().databaseConnectionProvider;

        System.out.println(dbConnProvider.toString());

        // Test database ends in _test so we don't accidentally run tests against a database not meant for test
        // Test DB is disposable and rebuilt every run to make sure we don't leave artifacts from failed tests
        Assert.assertTrue("Test database name MUST end with '_test'", dbConnProvider.getJdbcProperties().getProperty("jdbcUrl").endsWith("_test"));

        // Migrate
        flyway.setDataSource(dbConnProvider.getDataSource());
        flyway.setLocations("filesystem:db/migration", "filesystem:db/migration_test");
        flyway.clean();
        flyway.migrate();
        flyway.validate();
    }
}
