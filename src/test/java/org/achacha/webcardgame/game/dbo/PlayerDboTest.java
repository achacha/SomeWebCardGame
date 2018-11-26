package org.achacha.webcardgame.game.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlayerDboTest extends BaseInitializedTest {
    private PlayerDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(PlayerDbo.class);

    @Test
    void testGetPlayerForLogin() {
        List<PlayerDbo> players = factory.getByLoginId(TestDataConstants.JUNIT_USER_LOGINID);
        assertNotNull(players);
        assertEquals(1, players.size());

        PlayerDbo player = players.get(0);
        assertEquals(1200, player.getEnergy());
        assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
    }

    @Test
    void testGetPlayerForId() {
        PlayerDbo player = factory.getByLoginIdAndPlayerId(TestDataConstants.JUNIT_USER_LOGINID, TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(player);
        assertEquals(1200, player.getEnergy());
        assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
    }
}
