package org.achacha.base.db;

import org.achacha.base.global.Global;

import javax.annotation.Nullable;
import java.sql.Connection;

public abstract class BaseArchiveDboFactory<T extends BaseDbo> extends BaseDboFactory<T> {
    protected BaseArchiveDboFactory(Class<T> dboClass) {
        super(dboClass);
    }

    /**
     * Find Dbo by original id in archive table reusing an existing connection
     *
     * @param connection Connection
     * @param id long
     * @return LoginUserDbo or null if not found
     */
    @Nullable
    public T getByOriginalId(Connection connection, long id) {
        final String sql = Global.getInstance().getDatabaseManager().getSqlProvider()
                .builder("/sql/base/SelectByOriginalId.sql")
                .withToken("TABLE", table.schema()+"."+table.name())
                .build();
        try (
                JdbcSession session = Global.getInstance().getDatabaseManager().executeSqlDirect(
                        connection,
                        sql,
                        p -> p.setLong(1, id)
                )
        ) {
            if (session.getResultSet().next()) {
                return createFromResultSet(session.getResultSet());
            } else {
                LOGGER.warn("Failed to find object id={}", id);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find object", sqle);
        }

        return null;
    }

}
