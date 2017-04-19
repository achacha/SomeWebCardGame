package org.achacha.base.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Lambda interface to help initialize PreparedStatement
 */
public interface PreparedStatementSetter {
    void prepare(PreparedStatement pstmt) throws SQLException;
}
