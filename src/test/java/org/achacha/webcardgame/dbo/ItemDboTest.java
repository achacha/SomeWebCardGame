package org.achacha.webcardgame.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.ItemDbo;
import org.achacha.webcardgame.game.dbo.ItemDboFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ItemDboTest extends BaseInitializedTest {
    @Test
    public void testGetItemsForInventory() {
        List<ItemDbo> items = ItemDboFactory.getItemsForInventory(TestDataConstants.JUNIT_INVENTORY_ID);

        Assert.assertNotNull(items);
        Assert.assertEquals(6, items.size());
        Assert.assertEquals(31L, items.get(0).getQuantity());
    }
}
