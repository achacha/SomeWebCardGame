package org.achacha.base.db.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Connection provider using DB pool for a given JDBC connect URL
 */
public class DbPoolConnectionProvider extends JdbcDatabaseConnectionProvider {
    protected final HikariDataSource dataSource;

    public DbPoolConnectionProvider(Properties properties) {
        super(properties);

        HikariConfig config = new HikariConfig(properties);
        LOGGER.info("Setting all connections to transactional mode (autoCommit=false)");
        config.setAutoCommit(false);
        dataSource = new HikariDataSource(config);
    }

    @NotNull
    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    @NotNull
    @Override
    public Connection getConnection(boolean autoCommit) throws SQLException {
        int retries = 20;
        while (retries > 0) {
            try {
                Connection connection = dataSource.getConnection();
                if (!connection.isClosed()) {
                    connection.setAutoCommit(autoCommit);
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
