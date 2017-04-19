package org.achacha.base.db.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides connections to a JDBC based database
 */
public abstract class JdbcDatabaseConnectionProvider implements DatabaseConnectionProvider {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionProvider.class);

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
