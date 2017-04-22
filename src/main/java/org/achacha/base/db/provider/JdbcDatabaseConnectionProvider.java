package org.achacha.base.db.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides connections to a JDBC based database
 */
public abstract class JdbcDatabaseConnectionProvider implements DatabaseConnectionProvider {
    protected static final Logger LOGGER = LogManager.getLogger(DatabaseConnectionProvider.class);

    final String jdbcUrl;

    public JdbcDatabaseConnectionProvider(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        LOGGER.info("JDBC url={}", this.jdbcUrl);
    }

    /**
     * @return JDBC URL to use for connections
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }
}
