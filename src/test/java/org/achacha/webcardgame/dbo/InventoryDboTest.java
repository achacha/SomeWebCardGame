package org.achacha.webcardgame.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.InventoryDbo;
import org.achacha.webcardgame.game.dbo.InventoryDboFactory;
import org.junit.Assert;
import org.junit.Test;

public class InventoryDboTest extends BaseInitializedTest {
    @Test
    public void testGetInventoryForLogin() {
        InventoryDbo inventory = InventoryDboFactory.getByPlayerId(TestDataConstants.JUNIT_PLAYERID);
        Assert.assertNotNull(inventory);
        Assert.assertEquals(1200, inventory.getEnergy());
        Assert.assertEquals(6, inventory.getItems().size());
    }
}
