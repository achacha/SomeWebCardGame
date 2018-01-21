package org.achacha.webcardgame.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.junit.Assert;
import org.junit.Test;

public class AdventureDboTest extends BaseInitializedTest {
    @Test
    public void testGetItemsForInventory() {
        AdventureDbo adventure = AdventureDboFactory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);

        Assert.assertNotNull(adventure);
        Assert.assertEquals(TestDataConstants.JUNIT_ADVENTURE_ID, adventure.getId());

        Assert.assertNotNull(adventure.getEncounters());
        Assert.assertEquals(1, adventure.getEncounters().size());
        Assert.assertFalse(adventure.getEncounters().isEmpty());
    }
}
