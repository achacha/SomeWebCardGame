package org.achacha.test;

import org.achacha.base.db.BaseDbo;
import org.achacha.base.i18n.LocalizedKey;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Dbo for test_simple table
 */
public class TestSimpleDbo extends BaseDbo {

    protected long id;
    protected String name;
    protected LocalizedKey key;

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalizedKey getKey() {
        return key;
    }

    public TestSimpleDbo() {
        super();
    }

    public TestSimpleDbo(long id, String name, String key) {
        super();

        this.id = id;
        this.name = name;
        this.key = new LocalizedKey(key);
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.name = rs.getString("name");
        this.key = new LocalizedKey(rs.getString("key"));
    }
}
