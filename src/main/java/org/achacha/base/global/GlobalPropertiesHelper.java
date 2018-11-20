package org.achacha.base.global;

import org.achacha.base.db.JdbcSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Global properties loader populates GlobalProperties object from database
 */
public class GlobalPropertiesHelper {
    private static final Logger LOGGER = LogManager.getLogger(GlobalPropertiesHelper.class);

    /**
     * Read all properties and overlay over existing
     *
     * @param properties  to overlay the data onto
     * @param application or null for all
     */
    public static void load(GlobalProperties properties, String application) {
        if (null == application) {
            try (
                    JdbcSession triple = Global.getInstance().getDatabaseManager().executeSql(
                            null,
                            "/sql/GlobalProperties/SelectAll.sql"
                    )
            ) {
                populate(properties, triple.getResultSet());
            } catch (Exception sqle) {
                LOGGER.error("Failed to load global properties", sqle);
            }
        } else {
            try (
                    JdbcSession triple = Global.getInstance().getDatabaseManager().executeSql(
                            null,
                            "/sql/GlobalProperties/SelectAll.sql"
                    )
            ) {
                populate(properties, triple.getResultSet());
            } catch (Exception sqle) {
                LOGGER.error("Failed to read global properties for application={}", application, sqle);
            }
        }
    }

    /**
     * Read global properties from ResultSet
     * @param properties GlobalProperties
     * @param rs ResultSet
     * @throws SQLException
     */
    private static void populate(GlobalProperties properties, ResultSet rs) throws SQLException {
        while (rs.next()) {
            String name = rs.getString("name");
            String value = rs.getString("value");
            if (null != value) {
                properties.setProperty(name, value);
            } else {
                // null value in the database means remove this value if somehow it is in global file and needs removal at runtime
                // this is not expected to occur but may be used for very isolated cases
                properties.remove(name);
                LOGGER.warn("Removing global property since value is null: '{}'", name);
            }
        }
    }
}
