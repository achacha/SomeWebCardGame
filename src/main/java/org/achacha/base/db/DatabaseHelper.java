package org.achacha.base.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariDataSource;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseHelper {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseHelper.class);

    /** Regex for valid DB column names */
    public static final Pattern VALID_COLUMN_CHARS = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");

    /**
     * Execute simple SQL query and convert result to JsonArray
     *
     * @param sql statement
     * @return JsonArray or null if something bad happened
     * @throws SQLException
     */
    public static JsonArray selectToJsonArrayOfObjects(String sql) throws SQLException {
        return selectToJsonArrayOfObjects(sql, null);
    }

    /**
     * Execute simple SQL query and convert result to JsonArray
     *
     * @param sql statement
     * @param lookupMaps column -> Map used to do lookup/replace on the data
     * @return JsonArray or null if something bad happened
     * @throws SQLException
     */
    public static JsonArray selectToJsonArrayOfObjects(String sql, Map<String,Map<String,String>> lookupMaps) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        JsonArray ary = null;
        Connection conn = null;
        try {
            // Execute query
            conn = Global.getInstance().getDatabaseManager().getConnection();
            stmt = conn.createStatement();

            LOGGER.debug("SQL={}", sql);
            rs = stmt.executeQuery(sql);

            // Convert ResultSet into JSON
            ary = JsonHelper.toArrayOfObjects(rs, -1, lookupMaps);
        } finally {
            DatabaseManager.close(conn, stmt, rs);
        }
        return ary;
    }

    /**
     * Execute simple SQL query and convert result to JsonArray
     *
     * @param sql statement
     * @param lookupMaps column -> Map used to do lookup/replace on the data
     * @return JsonArray or null if something bad happened
     * @throws SQLException
     */
    public static ArrayList<Map<String,String>> selectToArrayOfMaps(String sql, Map<String,Map<String,String>> lookupMaps) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            // Execute query
            conn = Global.getInstance().getDatabaseManager().getConnection();
            stmt = conn.createStatement();

            LOGGER.debug("SQL: ", sql);
            rs = stmt.executeQuery(sql);

            // Convert ResultSet into JSON
            return toArrayOfMaps(rs, -1, lookupMaps);

        } finally {
            DatabaseManager.close(conn, stmt, rs);
        }
    }

    /**
     * Given a ResultSet convert it to JSON array of JSON Object
     * e.g. [ {name0:val00, name1:val01}, {name0:val10, name1:val11}, {name0:val20, name1:val21} ]
     *
     * @param rs ResultSet
     * @param lookupMaps column -> Map used to do lookup/replace on the data
     * @param n  use first n elements (-1 for everything)
     * @return Array of Maps
     */
    public static ArrayList<Map<String,String>> toArrayOfMaps(ResultSet rs, int n, Map<String,Map<String,String>> lookupMaps) {
        ArrayList<Map<String,String>> ary = new ArrayList<>();
        boolean noLimit = false;
        if (n == -1) {
            n = 1;
            noLimit = true;
        }

        try {
            // Build a list of column names
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<>();
            for (int col = 1; col <= numberOfColumns; ++col) {
                columnNames.add(rsMetaData.getColumnLabel(col));
            }

            // Iterate over the data
            while (rs.next() && n > 0) {
                Map<String,String> obj = new HashMap<>();
                for (int col = 1; col <= numberOfColumns; ++col) {
                    Object v = rs.getObject(col);
                    if (null != v) {
                        String columnName = columnNames.get(col - 1);
                        if (null != lookupMaps) {
                            // Lookup requested
                            Map<String,String> lookupMap = lookupMaps.get(columnName);
                            if (null != lookupMap) {
                                String lookedUpValue = lookupMap.get(v.toString());
                                if (null == lookedUpValue) {
                                    LOGGER.warn("Lookup not found for columnName={} and value={}", columnName, v);
                                }
                                else {
                                    v = lookedUpValue;
                                }
                            }
                        }
                        obj.put(columnNames.get(col - 1), v.toString());
                    }

                }
                ary.add(obj);

                // If no limit don't decrement, we wants n to always be >0
                if (!noLimit) --n;
            }
        } catch (Exception e) {
            throw new RuntimeException("SQL Exception", e);
        }
        return ary;
    }

    /**
     * Count rows in a given table
     * @param table String
     * @return long
     */
    public static long rowCount(String table) {
        final String sql = "SELECT COUNT(*) FROM "+ table;
        long count = 0;
        try (
            JdbcTuple triple = Global.getInstance().getDatabaseManager().executeSql(sql)
        ){
            if (triple.getResultSet().next()) {
                count = triple.getResultSet().getLong(1);
            }
        }
        catch(Exception ex) {
            throw new RuntimeException("Failed to count rows: "+table, ex);
        }
        return count;
    }

    /**
     * Checks if this is a valid column name
     * @param columnName String
     * @return true if valid
     */
    public static boolean isColumnNameValid(String columnName) {
        Matcher m = VALID_COLUMN_CHARS.matcher(columnName);
        return m.matches();
    }

    /**
     * Display SQL from the Statement
     *
     * @param stmt Statement
     * @return String toString from inner Statement
     */
    public static String toString(Statement stmt) {
        return stmt.toString();
    }

    /**
     * Display SQL with parameters for the PreparedStatement
     *
     * @param pstmt PreparedStatement
     * @return String toString with parameters
     */
    public static String toString(PreparedStatement pstmt) {
        return pstmt.toString();
    }

    /**
     * Escape ANSI SQL queries
     * Replace ' with ''
     *
     * @param in
     * @return Escaped string
     */
    public static String escapeString(String in) {
        return in.replace("\'", "\'\'");
    }

    /**
     * Populate JsonObject with DataSource properties
     * @param ds
     * @return
     */
    public static JsonObject toJsonObject(DataSource ds) {
        JsonObject obj = new JsonObject();
        if (ds instanceof HikariDataSource) {
            HikariDataSource hds = (HikariDataSource)ds;
            obj.addProperty("jdbcurl", hds.getJdbcUrl());
            obj.addProperty("class_name", hds.getDataSourceClassName());
            obj.addProperty("username", hds.getUsername());
            obj.addProperty("pool_name", hds.getPoolName());
            obj.addProperty("maximum_pool_size", hds.getMaximumPoolSize());
            obj.addProperty("minimum_idle", hds.getMinimumIdle());
            obj.addProperty("is_auto_commit", hds.isAutoCommit());
            obj.addProperty("is_read_only", hds.isReadOnly());
        }
        else {
            LOGGER.error("DataSource NOT supported");
            obj.addProperty("toString", ds.toString());
        }        return obj;
    }

    /**
     * Execute SQL which returns count()
     * @param sql String
     * @return long result of count, -1 of count SQL returned no result set
     * @throws SQLException
     */
    public static long countQuery(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Execute query
            conn = Global.getInstance().getDatabaseManager().getConnection();
            stmt = conn.createStatement();

            LOGGER.debug("SQL={}", sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        finally {
            DatabaseManager.close(conn, stmt, rs);
        }
        return -1;
    }

    public static void toHtmlTable(String sql, StringBuilder output) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Execute query
            conn = Global.getInstance().getDatabaseManager().getConnection();
            stmt = conn.createStatement();

            LOGGER.debug("SQL={}", sql);
            rs = stmt.executeQuery(sql);

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<>();
            for (int col = 1; col <= numberOfColumns; ++col) {
                columnNames.add(rsMetaData.getColumnLabel(col));
            }

            // Generate HTML table
            output.append("<table class='table table-sm table-bordered table-hover table-striped' style='width:100%; line-height:1.0;'>\n");

            output.append("<thead><tr>");
            for (String columnName : columnNames) {
                output.append("<th>");
                output.append(columnName);
                output.append("</th>");
            }
            output.append("</tr></thead>");

            output.append("<tbody>");
            while (rs.next()) {
                output.append("<tr>");
                for (String columnName : columnNames) {
                    output.append("<th>");
                    output.append(rs.getString(columnName));
                    output.append("</th>");
                }
                output.append("</tr>");
            }
            output.append("</tbody>");

            output.append("</table>");
        }
        catch(SQLException e) {
            LOGGER.error(e);
            output.append("<b>").append(e.toString()).append("</b>");
        }
        finally {
            DatabaseManager.close(conn, stmt, rs);
        }

    }
}
