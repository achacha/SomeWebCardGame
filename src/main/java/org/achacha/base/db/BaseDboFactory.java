package org.achacha.base.db;

import com.google.common.base.Preconditions;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;

/**
 * Base class for all DboFactory types
 * Contains an instance of the current DatabaseManager
 */
public class BaseDboFactory<T extends BaseIndexedDbo> {
    /**
     * Logger base on the backing object
     */
    protected final Logger LOGGER;

    /**
     * Dbo class this factory backs
     */
    protected final Class<T> clz;

    /**
     * Table for Dbo
     */
    protected final Table table;

    public BaseDboFactory(Class<T> clz) {
        this.clz = clz;
        this.LOGGER = LogManager.getLogger(clz);

        Table[] tables = clz.getDeclaredAnnotationsByType(Table.class);
        Preconditions.checkState(Preconditions.checkNotNull(tables).length > 0);
        this.table = tables[0];
    }

    /**
     * Given ResultSet create and populate object
     * @param rs ResultSet
     * @return T object or null if error occurred
     */
    @Nullable
    protected T createFromResultSet(ResultSet rs) {
        try {
            Constructor<? extends BaseIndexedDbo> ctor = clz.getDeclaredConstructor();
            T dbo = (T)ctor.newInstance();
            dbo.fromResultSet(rs);
            return dbo;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Dbo for clz="+clz, e);
        }
    }

    /**
     * Find Dbo by id
     *
     * @param id long
     * @return LoginUserDbo or null if not found
     */
    @Nullable
    public T byId(long id) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        final String sql = dbm.getSqlProvider()
                .builder("/sql/base/SelectById.sql")
                .withToken("TABLE", table.schema()+"."+table.name())
                .build();
        try (
                JdbcSession triple = Global.getInstance().getDatabaseManager().executeSqlDirect(
                        sql,
                        p -> p.setLong(1, id)
                )
        ) {
            if (triple.getResultSet().next()) {
                return createFromResultSet(triple.getResultSet());
            } else {
                LOGGER.warn("Failed to find login id={}", id);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find login", sqle);
        }

        return null;
    }

}
