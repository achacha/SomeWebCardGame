package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.ItemDbo;
import org.achacha.webcardgame.game.dbo.ItemDboFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemDboTest extends BaseInitializedTest {
    private ItemDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(ItemDbo.class);

    @Test
    void testGetItemsForInventory() {
        List<ItemDbo> items = factory.getItemsForInventory(TestDataConstants.JUNIT_INVENTORY_ID);

        assertNotNull(items);
        assertEquals(6, items.size());
        assertEquals(31L, items.get(0).getQuantity());
    }
}
