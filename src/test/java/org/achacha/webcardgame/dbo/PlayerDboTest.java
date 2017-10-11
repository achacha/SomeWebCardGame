package org.achacha.webcardgame.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class PlayerDboTest extends BaseInitializedTest {
    @Test
    public void testGetPlayerForLogin() {
        List<PlayerDbo> players = PlayerDboFactory.getByLoginId(TestDataConstants.JUNIT_LOGINID);
        Assert.assertNotNull(players);
        Assert.assertEquals(1, players.size());

        PlayerDbo player = players.get(0);
        Assert.assertEquals(1200, player.getEnergy());
        Assert.assertEquals(TestDataConstants.JUNIT_PLAYERID, player.getId());
    }
}
