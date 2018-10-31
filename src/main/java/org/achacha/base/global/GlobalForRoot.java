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
        final Properties dbProperties = getDbProperties();

        // Configure DatabaseManager
        LOGGER.info("ROOT: Creating database manager  dbProperties={}", dbProperties);
        databaseManager = new DatabaseManager(
                new DbPoolConnectionProvider(dbProperties),
                new ResourceSqlProvider()
        );
    }

    @Override
    public Properties getDbProperties() {
        final Properties dbProperties = new Properties();
        final String dbPrefix = "flyway.db.";


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
        return dbProperties;
    }

}
