package org.achacha.webcardgame.game.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerDboTest extends BaseInitializedTest {
    private PlayerDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(PlayerDbo.class);

    @Test
    void testGetPlayerForLogin() {
        List<PlayerDbo> players = factory.getByLoginId(TestDataConstants.JUNIT_USER_LOGINID);
        assertNotNull(players);
        assertTrue(players.size() > 0);  // Concurrent tests may be running that create players

        PlayerDbo player = players.get(0);
        assertEquals(1200, player.getInventory().getEnergy());
        assertEquals(5000, player.getInventory().getMaterials());
        assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
    }

    @Test
    void testGetPlayerForId() {
        PlayerDbo player = factory.getByLoginIdAndPlayerId(TestDataConstants.JUNIT_USER_LOGINID, TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(player);
        assertEquals(1200, player.getInventory().getEnergy());
        assertEquals(5000, player.getInventory().getMaterials());
        assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
    }

    @Test
    void testPlayerDeleteWithCascade() {
        // TODO:
    }
}
