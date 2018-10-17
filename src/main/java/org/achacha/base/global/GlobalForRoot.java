package org.achacha.base.global;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.provider.DbPoolConnectionProvider;
import org.achacha.base.db.provider.ResourceSqlProvider;

import java.util.Properties;

public class GlobalForRoot extends Global {
    public GlobalForRoot() {
        super("ROOT", null);
    }

    @Override
    public void initChild() {
    }

    @Override
    public void initDatabaseManager() {
        final Properties dbProperties = getDbProperties("");

        // Configure DatabaseManager
        LOGGER.info("Creating database manager");
        databaseManager = new DatabaseManager(
                new DbPoolConnectionProvider(dbProperties),
                new ResourceSqlProvider()
        );
    }
}
