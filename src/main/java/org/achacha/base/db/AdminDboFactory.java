package org.achacha.base.db;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.util.Set;
import java.util.TreeSet;

/**
 * Admin methods
 */
public class AdminDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(AdminDboFactory.class);

    public static Set<Long> getAllIds(Class<? extends BaseIndexedDbo> clz) {
        Table[] table = clz.getDeclaredAnnotationsByType(Table.class);
        Preconditions.checkNotNull(table);
        Preconditions.checkState(table.length > 0);

        String tableName = table[0].schema()+"."+table[0].name();

        final String SQL = dbm.getSqlProvider()
                .builder("/sql/_admin/selectAllIds.sql")
                .withToken("TABLE", tableName)
                .build();

        Set<Long> ids = new TreeSet<>();
        try (JdbcSession session = dbm.executeSqlDirect(SQL)) {
            while (session.getResultSet().next()) {
                ids.add(session.getResultSet().getLong("id"));
            }
        }
        catch(Exception e) {
            LOGGER.error("Failed to get ids for tableName="+tableName, e);
        }
        return ids;
    }

    public static BaseIndexedDbo getDboById(Class<? extends BaseIndexedDbo> clz, long id) {
        return DatabaseManager.loadObjectById(clz, id);
    }
}
