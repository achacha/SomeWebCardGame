package org.achacha.base.db.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Connection provider using DB pool
 */
public class DbPoolConnectionProvider extends JdbcDatabaseConnectionProvider {
    protected final HikariDataSource dataSource;

    public DbPoolConnectionProvider(String jdbcUrl, Properties properties) {
        super(jdbcUrl);

        HikariConfig config = new HikariConfig(properties);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(true);
    }

    @Override
    public Connection getConnection(boolean autoCommit) throws SQLException {
        int retries = 20;
        while (retries > 0) {
            try {
                Connection connection = dataSource.getConnection();
                if (!connection.isClosed()) {
                    // Connection is not closed, set transactional if so requested
                    if (!autoCommit)
                        connection.setAutoCommit(false);

                    return connection;
                } else {
                    LOGGER.info("Detected database closed connection, retry #{}", retries);
                }
            } catch (SQLException ex) {
                LOGGER.error("Failed to get database connection (retries left: " + retries + ") for '" + dataSource.toString(), ex);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    throw new SQLException("Failed to get database connection, interrupted while waiting to get connection", ie);
                }
            }
            --retries;
        }
        throw new SQLException("Failed to get database connection, giving up after retries!");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

}
