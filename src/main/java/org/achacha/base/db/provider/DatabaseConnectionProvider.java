package org.achacha.base.db.provider;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides connections to a database
 */
public interface DatabaseConnectionProvider {
    /**
     * Default auto-commit is true
     * @return Connection
     * @throws SQLException if error occurs
     */
    @Nonnull
    Connection getConnection() throws SQLException;

    /**
     * Allow explicit control of auto-commit on the connection
     * This is ususally used to turn off auto-commit and allow transactions
     * @return Connection
     * @throws SQLException if error occurs
     */
    @Nonnull
    Connection getConnection(boolean autoCommit) throws SQLException;
}
