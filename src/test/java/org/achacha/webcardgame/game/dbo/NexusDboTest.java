package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NexusDboTest extends BaseInitializedTest {
    @Test
    void crudNexus() throws SQLException {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();

        PlayerDbo player = createNewTestPlayer("test_nexus_crud");

        try (Connection connection = dbm.getConnection()) {
            NexusDbo nexus = dbm.<NexusDboFactory>getFactory(NexusDbo.class).getByPlayerId(connection, player.getId());
            assertNull(nexus);

            nexus = new NexusDbo();
            nexus.setPlayerId(player.getId());
            nexus.energyGathererType = 1;
            nexus.resourceProcessingType = 2;
            nexus.level = 3;
            nexus.insert(connection);
            connection.commit();

            NexusDbo insertedNexus = dbm.<NexusDboFactory>getFactory(NexusDbo.class).getByPlayerId(connection, player.getId());
            assertNotNull(insertedNexus);
            assertEquals(nexus.getPlayerId(), insertedNexus.getPlayerId());
            assertEquals(1, nexus.energyGathererType);
            assertEquals(2, nexus.resourceProcessingType);
            assertEquals(3, nexus.level);

            dbm.<NexusDboFactory>getFactory(NexusDbo.class).deleteById(connection, nexus.getId());
            connection.commit();
            assertNull(dbm.<NexusDboFactory>getFactory(NexusDbo.class).getByPlayerId(connection, player.getId()));
        }
    }
}