package org.achacha.base.db;

import org.achacha.base.db.provider.JdbcDatabaseConnectionProvider;
import org.achacha.base.db.provider.SqlProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handle database connections and execute queries
 * Uses JdbcTuple for simple queries
 *
 * @see JdbcTuple for autoclosing tuple of Connection/Statement/ResultSet
 */
public class DatabaseManager {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseManager.class);

    /**
     * Provider of Connection
     */
    final JdbcDatabaseConnectionProvider databaseConnectionProvider;

    /**
     * SQL provider
     */
    private final SqlProvider sqlProvider;

    /**
     * Construct a database manager with a custom providers
     * @param databaseConnectionProvider Connection provider
     * @param sqlProvider SQL provider
     */
    public DatabaseManager(
            @Nonnull JdbcDatabaseConnectionProvider databaseConnectionProvider,
            @Nonnull SqlProvider sqlProvider
    ) {
        this.databaseConnectionProvider = databaseConnectionProvider;
        this.sqlProvider = sqlProvider;
    }

    /**
     * Get database connection from Global DataSource
     * @return Connection
     * @throws SQLException
     */
    @Nonnull
    public Connection getConnection() throws SQLException {
        return databaseConnectionProvider.getConnection();
    }

    /**
     * Get database connection from Global DataSource
     * @param autoCommit true=transactions, false=required manual commit
     * @return Connection
     * @throws SQLException
     */
    @Nonnull
    public Connection getConnection(boolean autoCommit) throws SQLException {
        return databaseConnectionProvider.getConnection(autoCommit);
    }

    /**
     * Prepare PreparedStatement with parameters using lambda function
     *
     * try (
     *      Connection connection = DatabaseManager.getConnection();
     *      PreparedStatement stmt = DatabaseManager.prepareStatement(connection, SQL, pstmt -> {
     *          pstmt.setString(1, "foo");
     *      });
     *      ResultSet rs = stmt.executeQuery()
     * ) {
     *      while (rs.next()) {
     *      // ...
     * } catch (Exception e) {
     *      e.printStackTrace();
     * }
     *
     * @param conn Connection
     * @param resourcePath String SQL resource path
     * @param setter Setter that will set the parameters in the PreparedStatement
     * @return PreparedStatement with parameters set
     * @throws SQLException
     * @throws IOException
     */
    public PreparedStatement prepareStatement(Connection conn, String resourcePath, PreparedStatementSetter setter) throws IOException, SQLException {
        String sql = sqlProvider.get(resourcePath);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setter.prepare(pstmt);
        LOGGER.debug("SQL: " + DatabaseHelper.toString(pstmt));
        return pstmt;
    }

    /**
     * Prepare PreparedStatement with parameters using lambda function
     *
     * try (
     *      Connection connection = DatabaseManager.getConnection();
     *      PreparedStatement stmt = DatabaseManager.prepareStatement(connection, SQL, pstmt -> {
     *          pstmt.setString(1, "foo");
     *      });
     *      ResultSet rs = stmt.executeQuery()
     * ) {
     *      while (rs.next()) {
     *      // ...
     * } catch (Exception e) {
     *      e.printStackTrace();
     * }
     *
     * @param conn Connection
     * @param sql String SQL with parameters to set
     * @param setter Setter that will set the parameters in the PreparedStatement
     * @return PreparedStatement with parameters set
     * @throws SQLException
     */
    public static PreparedStatement prepareStatementDirect(Connection conn, String sql, PreparedStatementSetter setter) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setter.prepare(pstmt);
        LOGGER.debug("SQL: " + DatabaseHelper.toString(pstmt));
        return pstmt;
    }

    /**
     * Close statement and connection and log errors if any
     * Prefer using try-with-resource to avoid calling this directly
     * @param connection Connection (or null if none)
     * @param statement Statement (or null if none)
     * @param resultSet ResultSet (or null if none)
     */
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (connection != null)
                connection.setAutoCommit(true);
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
            if (connection != null) {
                connection.close();
            }
        }
        catch(SQLException ex) {
            LOGGER.error("Failed to close", ex);
        }
    }

    /**
     * Safe rollback of Connection
     * If connection is null, does nothing
     *
     * @param connection Connection
     */
    public static void rollback(Connection connection) {
        try {
            if (connection != null)
                connection.rollback();
        } catch (SQLException sqle) {
            LOGGER.error("Failed to rollback transaction", sqle);
        }
    }

    /**
     * Create Connection/PreparedStatement/ResultSet
     * Uses setter to set parameters on PreparedStatement
     *
     * @param sql String SQL
     * @param setter Setter for PreparedStatement
     * @return JdbcTuple
     * @throws SQLException
     */
    public JdbcTuple executeSqlDirect(String sql, PreparedStatementSetter setter) throws Exception {
        JdbcTuple triple = new JdbcTuple();
        triple.connection = databaseConnectionProvider.getConnection();
        triple.statement = DatabaseManager.prepareStatementDirect(triple.connection, sql, setter);
        triple.resultSet = ((PreparedStatement)triple.statement).executeQuery();
        return triple;
    }

    /**
     * Create Connection/PreparedStatement/ResultSet
     * Uses setter to set parameters on PreparedStatement
     *
     * @param resourcePath String SQL resource classpath
     * @param setter Setter for PreparedStatement
     * @return JdbcTuple
     * @throws SQLException
     */
    public JdbcTuple executeSql(String resourcePath, PreparedStatementSetter setter) throws Exception {
        JdbcTuple triple = new JdbcTuple();
        String sql = sqlProvider.get(resourcePath);
        triple.connection = databaseConnectionProvider.getConnection();
        triple.statement = DatabaseManager.prepareStatementDirect(triple.connection, sql, setter);
        triple.resultSet = ((PreparedStatement)triple.statement).executeQuery();
        return triple;
    }

    /**
     * Create JdbcTuple with Connection/Statement/ResultSet
     * Use with static SQL
     *
     * @param sql String SQL
     * @return JdbcTuple
     * @throws SQLException
     */
    public JdbcTuple executeSqlDirect(String sql) throws Exception {
        JdbcTuple triple = new JdbcTuple();
        triple.connection = databaseConnectionProvider.getConnection();
        triple.statement = triple.connection.createStatement();
        triple.resultSet = triple.statement.executeQuery(sql);
        return triple;
    }

    /**
     * Create JdbcTuple with Connection/Statement/ResultSet
     * Use with static SQL
     *
     * @param resourcePath String SQL resource classpath
     * @return JdbcTuple
     * @throws SQLException
     */
    public JdbcTuple executeSql(String resourcePath) throws Exception {
        JdbcTuple triple = new JdbcTuple();
        String sql = sqlProvider.get(resourcePath);
        triple.connection = databaseConnectionProvider.getConnection();
        triple.statement = triple.connection.createStatement();
        triple.resultSet = triple.statement.executeQuery(sql);
        return triple;
    }
}
