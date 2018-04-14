package org.achacha.base.db.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Provides connections to a JDBC based database
 */
public abstract class JdbcDatabaseConnectionProvider implements DatabaseConnectionProvider {
    protected static final Logger LOGGER = LogManager.getLogger(DatabaseConnectionProvider.class);

    protected final Properties jdbcProperties;

    public JdbcDatabaseConnectionProvider(Properties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    /**
     * @return JDBC URL to use for connections
     */
    public Properties getJdbcProperties() {
        return jdbcProperties;
    }

    /**
     * @return DataSource
     */
    public abstract DataSource getDataSource();

}
