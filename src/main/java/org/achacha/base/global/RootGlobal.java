package org.achacha.base.global;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.provider.DbPoolConnectionProvider;
import org.achacha.base.db.provider.ResourceSqlProvider;

import java.util.Properties;

public class RootGlobal extends Global {
    public RootGlobal() {
        super("ROOT", null);
    }

    @Override
    public void initChild() {

    }

    @Override
    public void initDatabaseManager() {
        String jdbcUrl = properties.getProperty("db.jdbc.url");
        Properties dbProperties = new Properties();
        dbProperties.setProperty("jdbcUrl", properties.getProperty("db.jdbc.url"));
        dbProperties.setProperty("username", properties.getProperty("db.jdbc.user"));
        dbProperties.setProperty("password", properties.getProperty("db.jdbc.password"));
        databaseManager = new DatabaseManager(
                new DbPoolConnectionProvider(jdbcUrl, dbProperties),
                new ResourceSqlProvider()
        );
    }
}
