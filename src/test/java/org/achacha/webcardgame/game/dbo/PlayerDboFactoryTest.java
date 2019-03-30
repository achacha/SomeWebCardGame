package org.achacha.webcardgame.game.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerDboFactoryTest {
    private PlayerDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(PlayerDbo.class);

    @Test
    void testGetPlayerForLogin() throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            List<PlayerDbo> players = factory.getByLoginId(connection, TestDataConstants.JUNIT_USER_LOGINID);
            assertNotNull(players);

            assertTrue(players.size() > 2);

            PlayerDbo player0 = players.get(0);
            assertEquals("JUNIT_PLAYER1", player0.getName());
            assertEquals(1200, player0.getInventory().getEnergy());
            assertEquals(5000, player0.getInventory().getMaterials());
            assertEquals(TestDataConstants.JUNIT_PLAYER__ID1, player0.getId());

            PlayerDbo player1 = players.get(1);
            assertEquals("JUNIT_PLAYER2", player1.getName());
            assertEquals(1000, player1.getInventory().getEnergy());
            assertEquals(500, player1.getInventory().getMaterials());
            assertEquals(TestDataConstants.JUNIT_PLAYER__ID2, player1.getId());
        }
    }

    @Test
    void testGetPlayerForId() throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = factory.getByLoginIdAndPlayerId(connection, TestDataConstants.JUNIT_USER_LOGINID, TestDataConstants.JUNIT_PLAYER__ID1);
            assertNotNull(player);
            assertEquals("JUNIT_PLAYER1", player.getName());
            assertEquals(1200, player.getInventory().getEnergy());
            assertEquals(5000, player.getInventory().getMaterials());
            assertEquals(TestDataConstants.JUNIT_PLAYER__ID1, player.getId());
        }
    }

}