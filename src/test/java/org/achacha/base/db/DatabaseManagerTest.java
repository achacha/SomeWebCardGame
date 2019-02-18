package org.achacha.base.db;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseManagerTest extends BaseInitializedTest {
    @Test
    void testInitialization() {
        assertNotNull(Global.getInstance());
        DatabaseManager dm = Global.getInstance().getDatabaseManager();
        assertNotNull(dm);
    }

    @Test
    void getTimestampNow() throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            Timestamp now = DatabaseManager.getTimestampNow(connection);
            assertNotNull(now);
        }
    }
}
