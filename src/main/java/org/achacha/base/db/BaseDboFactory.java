package org.achacha.base.db;

import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Base class for all DboFactory types
 * Contains an instance of the current DatabaseManager
 */
public abstract class BaseDboFactory<T extends BaseIndexedDbo> {
    /**
     * Logger base on the backing object
     */
    protected final Logger LOGGER;

    /**
     * Dbo class this factory backs
     */
    protected final Class<T> dboClass;

    /**
     * Table for Dbo
     */
    protected final Table table;

    public BaseDboFactory(Class<T> dboClass) {
        this.dboClass = dboClass;
        this.LOGGER = LogManager.getLogger(dboClass);

        this.table = DboHelper.getTableAnnotation(dboClass);
    }

    public Class<T> getDboClass() {
        return dboClass;
    }

    /**
     * Given ResultSet create and populate object
     * @param rs ResultSet
     * @return T object or null if error occurred
     */
    @Nullable
    protected T createFromResultSet(ResultSet rs) {
        try {
            Constructor<? extends BaseIndexedDbo> ctor = dboClass.getDeclaredConstructor();
            T dbo = (T)ctor.newInstance();
            dbo.fromResultSet(rs);
            return dbo;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Dbo for dboClass="+ dboClass, e);
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
                LOGGER.warn("Failed to find object id={}", id);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find object", sqle);
        }

        return null;
    }

    /**
     * Delete by id
     * @param id long
     */
    public void deleteById(long id) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        final String sql = dbm.getSqlProvider()
                .builder("/sql/base/DeleteById.sql")
                .withToken("TABLE", table.schema()+"."+table.name())
                .build();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatementDirect(
                        connection,
                        sql,
                        p -> p.setLong(1, id))
        ) {
            if (pstmt.executeUpdate() != 1) {
                LOGGER.warn("Unable to delete, id={}", id);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to delete, id="+id, e);
        }
    }



}
