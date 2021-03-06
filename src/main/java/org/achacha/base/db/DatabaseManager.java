package org.achacha.base.db;

import com.google.common.base.Preconditions;
import org.achacha.base.db.provider.JdbcDatabaseConnectionProvider;
import org.achacha.base.db.provider.SqlProvider;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handle database connections and execute queries
 * Uses JdbcSession for simple queries
 *
 * @see JdbcSession for autoclosing tuple of Connection/Statement/ResultSet
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
     * Dbo factories mapped by class
     */
    private Map<Class<? extends BaseDbo>, BaseDboFactory<? extends BaseDbo>> factories = new HashMap<>();

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

        // Get all Dbo factories and map by class they back
        Set<Class<? extends BaseDboFactory>> factoryClasses = DboClassHelper.getAllDboFactories();
        factoryClasses.forEach(clz->{
            BaseDboFactory<? extends BaseDbo> factory = null;
            try {
                factory = clz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to create Dbo factory, clz="+clz, e);
            }
            this.factories.put(factory.getDboClass(), factory);
        });
    }

    /**
     * Get associated factory
     * @param clz Dbo class
     * @param <T> Factory class
     * @return Factory for Dbo
     */
    public <T extends BaseDboFactory<? extends BaseDbo>> T getFactory(Class<? extends BaseDbo> clz) {
        return (T)factories.get(clz);
    }

    /**
     * Create a session and preform a select
     *
     * @param connection Connection to reuse or null to create a new one
     * @param key that contains SQL
     * @param setter if null with use a Statement to perform query otherwise will use a PreparedStatement and apply setter
     * @return JdbcSession with query executed and ResultSet ready to use
     * @throws SQLException if anything goes wrong
     *
     * try (JdbcSession session = dbm.select("/path/to/my.sql", null)) {
     *     ResultSet rs = session.getResultSet();
     *     while (rs.next()) {
     *         //...
     *     }
     * }
     *
     * with parameters in SQL
     *
     * try (JdbcSession session = dbm.select("/path/to/my.sql", ps->{
     *                                                             ps.setInt(1, 123);
     *                                                             ps.setString(2, "second param");
     *                                                          }
     * )) {
     *     ResultSet rs = session.getResultSet();
     *     while (rs.next()) {
     *         //...
     *     }
     * }
     *
     */
    public JdbcSession select(Connection connection, String key, @Nullable PreparedStatementSetter setter) throws SQLException {
        String sql = sqlProvider.get(key);
        JdbcSession session = new JdbcSession();
        session.connection = connection != null ? connection : databaseConnectionProvider.getConnection();
        if (setter != null) {
            // Prepared statement with parameters
            PreparedStatement ps = session.connection.prepareStatement(sql);
            setter.prepare(ps);
            session.statement = ps;
            session.resultSet = ps.executeQuery();
        }
        else {
            // No parameters, can use Statement
            session.statement = session.connection.createStatement();
            session.resultSet = session.statement.executeQuery(sql);
        }
        return session;
    }

    /**
     * Update or insert
     * For transactional queries, session.connection.commit() must be called
     *
     * @param connection Connection to reuse or null to create a new one
     * @param key that contains SQL
     * @param setter sets parameters on PreparedStatement, if null SQL does not expect params
     * @return JdbcSession
     */
    public JdbcSession update(Connection connection, String key, PreparedStatementSetter setter) throws SQLException {
        String sql = sqlProvider.get(key);
        JdbcSession session = new JdbcSession();
        session.connection = connection != null ? connection : databaseConnectionProvider.getConnection();

        PreparedStatement ps = session.connection.prepareStatement(sql);
        if (setter != null)
            setter.prepare(ps);
        session.statement = ps;

        session.updateResult = ps.executeUpdate();
        return session;
    }

    /**
     * @return JdbcDatabaseConnectionProvider
     */
    public JdbcDatabaseConnectionProvider getDatabaseConnectionProvider() {
        return databaseConnectionProvider;
    }

    /**
     * @return SqlProvider
     */
    public SqlProvider getSqlProvider() {
        return sqlProvider;
    }

    /**
     * Get database connection from Global DataSource
     * @return Connection
     * @throws SQLException if fails to get connection
     */
    @Nonnull
    public Connection getConnection() throws SQLException {
        return databaseConnectionProvider.getConnection();
    }

    /**
     * Get database connection from Global DataSource
     * @param autoCommit if true auto-commits, if false requires an explicit commit
     * @return Connection
     * @throws SQLException if fails to get connection
     */
    @Nonnull
    public Connection getConnection(boolean autoCommit) throws SQLException {
        return databaseConnectionProvider.getConnection(autoCommit);
    }

    /**
     * Prepare PreparedStatement with parameters using lambda function
     *
     * {@code
     * try (
     *      Connection connection = DatabaseManager.getConnection();
     *      PreparedStatement stmt = DatabaseManager.prepareStatement(connection, SQL, pstmt -> {
     *          pstmt.setString(1, "foo");
     *      });
     *      ResultSet rs = stmt.executeQuery()
     * ) {
     *      while (rs.next()) {
     *          // ...
     *      }
     * } catch (Exception e) {
     *      e.printStackTrace();
     * }
     * }
     *
     * @param connection Connection to reuse or null to create a new one
     * @param resourcePath String SQL resource path, looked up via SQLProvider
     * @param setter Setter that will set the parameters in the PreparedStatement
     * @return PreparedStatement with parameters set
     * @throws SQLException if fails to prepare statement
     * @see SqlProvider
     */
    public PreparedStatement prepareStatement(Connection connection, String resourcePath, PreparedStatementSetter setter) throws SQLException {
        String sql = sqlProvider.get(resourcePath);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        if (setter != null)
            setter.prepare(pstmt);

        LOGGER.debug("SQL: " + DatabaseHelper.toString(pstmt));
        return pstmt;
    }

    /**
     * Prepare PreparedStatement with parameters using lambda function
     * This method does not lookup SQL in resource bundle and uses SQL provided
     *
     * {@code
     * try (
     *      Connection connection = DatabaseManager.getConnection();
     *      PreparedStatement stmt = DatabaseManager.prepareStatementDirect(connection, SQL, pstmt -> {
     *          pstmt.setString(1, "foo");
     *      });
     *      ResultSet rs = stmt.executeQuery()
     * ) {
     *      while (rs.next()) {
     *          // ...
     *      }
     * } catch (Exception e) {
     *      e.printStackTrace();
     * }
     * }
     *
     * @param connection Connection to reuse or null to create a new one
     * @param sql String Actual SQL with parameters to set
     * @param setter Setter that will set the parameters in the PreparedStatement
     * @return PreparedStatement with parameters set
     * @throws SQLException is fails to prepare statement
     */
    public PreparedStatement prepareStatementDirect(Connection connection, String sql, PreparedStatementSetter setter) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement(sql);
        if (setter != null)
            setter.prepare(pstmt);

        LOGGER.debug("SQL: " + DatabaseHelper.toString(pstmt));
        return pstmt;
    }

    /**
     * Close statement and connection and log errors if any
     * Prefer using try-with-resource to avoid calling this directly
     *
     * @param connection Connection (or null if none)
     * @param statement Statement (or null if none)
     * @param resultSet ResultSet (or null if none)
     */
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
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
     * Create PreparedStatement/ResultSet
     * Uses setter to set parameters on PreparedStatement
     * SQL provided is used directly without any lookup
     *
     * @param connection Connection to reuse or null to create a new one
     * @param sql String Actual SQL
     * @param setter Setter for PreparedStatement
     * @return JdbcSession
     * @throws SQLException if fails to execute SQL
     */
    public JdbcSession executeSqlDirect(Connection connection, String sql, PreparedStatementSetter setter) throws SQLException {
        JdbcSession triple = new JdbcSession();
        triple.connection = connection != null ? connection : databaseConnectionProvider.getConnection();
        triple.statement = prepareStatementDirect(triple.connection, sql, setter);
        triple.resultSet = ((PreparedStatement)triple.statement).executeQuery();
        return triple;
    }

    /**
     * Create PreparedStatement/ResultSet
     * Uses setter to set parameters on PreparedStatement
     *
     * @param connection Connection to reuse or null to create a new one
     * @param resourcePath String SQL resource via SqlProvider
     * @param setter Setter for PreparedStatement
     * @return JdbcSession
     * @throws SQLException if execute fails
     * @see SqlProvider
     */
    public JdbcSession executeSql(Connection connection, String resourcePath, PreparedStatementSetter setter) throws SQLException {
        JdbcSession triple = new JdbcSession();
        String sql = sqlProvider.get(resourcePath);
        triple.connection = connection != null ? connection : databaseConnectionProvider.getConnection();
        triple.statement = prepareStatementDirect(triple.connection, sql, setter);
        triple.resultSet = ((PreparedStatement)triple.statement).executeQuery();
        return triple;
    }

    /**
     * Create JdbcSession with Connection/Statement/ResultSet
     * Use with static SQL
     *
     * @param connection Connection to reuse or null to create a new one
     * @param sql String SQL
     * @return JdbcSession
     * @throws SQLException if fails to execit SQL
     */
    public JdbcSession executeSqlDirect(Connection connection, String sql) throws SQLException {
        JdbcSession triple = new JdbcSession();
        triple.connection = connection != null ? connection : databaseConnectionProvider.getConnection();
        triple.statement = triple.connection.createStatement();
        LOGGER.debug("SQL={}", sql);
        triple.resultSet = triple.statement.executeQuery(sql);
        return triple;
    }

    /**
     * Create JdbcSession with Statement/ResultSet
     * Use with static SQL
     *
     * @param connection Connection to reuse or null to create a new one
     * @param resourcePath String SQL resource classpath
     * @return JdbcSession
     * @throws SQLException if fails to execute SQL
     */
    public JdbcSession executeSql(Connection connection, String resourcePath) throws SQLException {
        JdbcSession triple = new JdbcSession();
        String sql = sqlProvider.get(resourcePath);
        triple.connection = connection != null ? connection : databaseConnectionProvider.getConnection();
        triple.statement = triple.connection.createStatement();
        LOGGER.debug("SQL={}", sql);
        triple.resultSet = triple.statement.executeQuery(sql);
        return triple;
    }

    /**
     * Get now() from database, it's the official and final reference on time
     * @param connection Connection
     * @return Timestamp
     * @throws SQLException if unable to get now() from database
     */
    @Nonnull
    public static Timestamp getTimestampNow(Connection connection) throws SQLException {
        try (
                PreparedStatement ps = Global.getInstance().getDatabaseManager().prepareStatement(connection, "/sql/base/GetTimestampNow.sql", null);
                ResultSet rs = ps.executeQuery()
        ) {
            Preconditions.checkState(rs.next());
            return rs.getTimestamp(1);
        }
    }

}
