package org.achacha.base.db;

import com.google.common.base.Preconditions;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.util.Set;
import java.util.TreeSet;

/**
 * Admin methods
 */
public class AdminDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(AdminDboFactory.class);

    public static Set<Long> getAllIds(Class<? extends BaseIndexedDbo> clz) {
        Table[] table = clz.getDeclaredAnnotationsByType(Table.class);
        Preconditions.checkNotNull(table);
        Preconditions.checkState(table.length > 0);

        String tableName = table[0].schema()+"."+table[0].name();

        final String SQL = Global.getInstance().getDatabaseManager().getSqlProvider()
                .builder("/sql/_admin/selectAllIds.sql")
                .withToken("TABLE", tableName)
                .build();

        Set<Long> ids = new TreeSet<>();
        try (JdbcSession session = Global.getInstance().getDatabaseManager().executeSqlDirect(null, SQL)) {
            while (session.getResultSet().next()) {
                ids.add(session.getResultSet().getLong("id"));
            }
        }
        catch(Exception e) {
            LOGGER.error("Failed to get ids for tableName="+tableName, e);
        }
        return ids;
    }
}
