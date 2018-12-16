package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.data.ItemType;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemDboTest extends BaseInitializedTest {
    @Test
    void testGetItemsForInventory() throws SQLException {
        PlayerDbo player = createNewTestPlayer();
        InventoryDbo inventory = Preconditions.checkNotNull(player).getInventory();
        List<ItemDbo> items = Preconditions.checkNotNull(inventory).items;
        assertNotNull(items);  // New player should have an inventory with non-null items container
        assertEquals(0, items.size());  // New player should have empty inventory

        // Add to inventory and check it is persisted, then remove
        items.add(ItemDbo.builder(inventory, ItemType.ElementOrange).build());

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            inventory.update(connection);
            connection.commit();
        }

        // Load it again and verify it was saved
        InventoryDbo loadedInventory = Global.getInstance().getDatabaseManager().<InventoryDboFactory>getFactory(InventoryDbo.class).getByPlayerId(player.id);
        assertNotNull(loadedInventory);
        assertNotNull(loadedInventory.items);
        assertEquals(1, loadedInventory.items.size());
        assertEquals(inventory.toJsonObject().toString(), loadedInventory.toJsonObject().toString());
    }
}
