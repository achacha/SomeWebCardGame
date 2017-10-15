package org.achacha.base.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Contains DB objects that close when done via AutoCloseable using try-with-resources
 *
 * {@code
 *    final String SQL = "select * from NVPAIR where name=?";
 *    try (
 *           JdbcTuple triple = JdbcTuple.executeSql(SQL, pstmt -> { pstmt.setString(1, "foo"); })
 *    ){
 *       while (triple.getResultSet().next()) {
 *           System.out.println(triple.getResultSet().getString("name") + "=" + triple.getResultSet().getString("value"));
 *       }
 *    } catch (Exception e) {
 *       e.printStackTrace();
 *    }
 * }
 *
 * NOTE: if used without try-with-resources, you have to call close() explicitly
 */
public class JdbcTuple implements AutoCloseable {
    Connection connection;
    Statement statement;
    ResultSet resultSet;

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * @throws Exception which is an SQLException but thrown as Exception to adhere to the AutoCloseable interface
     */
    @Override
    public void close() throws Exception {
        if (resultSet != null)
            resultSet.close();
        if (statement != null)
            statement.close();
        if (connection != null)
            connection.close();
    }
}
