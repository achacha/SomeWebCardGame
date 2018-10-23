package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class PlayerDboTest extends BaseInitializedTest {
    private PlayerDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(PlayerDbo.class);

    @Test
    public void testGetPlayerForLogin() {
        List<PlayerDbo> players = factory.getByLoginId(TestDataConstants.JUNIT_LOGINID);
        Assert.assertNotNull(players);
        Assert.assertEquals(1, players.size());

        PlayerDbo player = players.get(0);
        Assert.assertEquals(1200, player.getEnergy());
        Assert.assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
    }

    @Test
    public void testGetPlayerForId() {
        PlayerDbo player = factory.getByLoginIdAndPlayerId(TestDataConstants.JUNIT_LOGINID, TestDataConstants.JUNIT_PLAYER__ID);
        Assert.assertNotNull(player);
        Assert.assertEquals(1200, player.getEnergy());
        Assert.assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
    }
}
