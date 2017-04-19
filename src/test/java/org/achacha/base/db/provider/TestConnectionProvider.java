package org.achacha.base.db.provider;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Connection provider using pool
 */
public class TestConnectionProvider extends JdbcDatabaseConnectionProvider {
    private final Properties properties;

    public TestConnectionProvider(String jdbcUrl, Properties properties) {
        super(jdbcUrl);
        this.properties = properties;
    }

    @Override
    @Nonnull
    public Connection getConnection() throws SQLException {
        // TODO: Wrap Connection to allow debug tracing
        return DriverManager.getConnection(jdbcUrl, properties);
    }

    @Override
    @Nonnull
    public Connection getConnection(boolean autoCommit) throws SQLException {
        // TODO: Wrap Connection to allow debug tracing
        Connection conn =  DriverManager.getConnection(jdbcUrl, properties);
        conn.setAutoCommit(autoCommit);
        return conn;
    }

}
