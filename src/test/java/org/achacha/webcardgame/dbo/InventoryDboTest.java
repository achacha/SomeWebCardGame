package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.InventoryDbo;
import org.achacha.webcardgame.game.dbo.InventoryDboFactory;
import org.junit.Assert;
import org.junit.Test;

public class InventoryDboTest extends BaseInitializedTest {
    private InventoryDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(InventoryDbo.class);

    @Test
    public void testGetInventoryForLogin() {
        InventoryDbo inventory = factory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);
        Assert.assertNotNull(inventory);
        Assert.assertEquals(6, inventory.getItems().size());
    }
}
