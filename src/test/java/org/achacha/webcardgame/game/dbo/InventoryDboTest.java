package org.achacha.webcardgame.game.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.data.ItemType;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventoryDboTest extends BaseInitializedTest {
    private InventoryDboFactory inventoryFactory = Global.getInstance().getDatabaseManager().getFactory(InventoryDbo.class);

    @Test
    void testGetInventoryForExistingLogin() throws SQLException {
        // Default player has an inventory
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            InventoryDbo inventory = inventoryFactory.getByPlayerId(connection, TestDataConstants.JUNIT_PLAYER__ID);
            assertNotNull(inventory);
        }
    }

    @Test
    void testUpdate() throws SQLException {
        PlayerDbo player = createNewTestPlayer("test_inventory_update");
        InventoryDbo inventory = player.inventory;

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            // Clear inventory
            inventory.clear();
            inventory.update(connection);
            connection.commit();

            // Verify clear
            InventoryDbo inventoryCleared = inventoryFactory.getByPlayerId(connection, player.getId());
            assertNotNull(inventoryCleared);
            assertEquals(inventory.toJsonObject(), inventoryCleared.toJsonObject());

            // Modify and save
            inventory.energy = 100;
            inventory.materials = 2400;
            inventory.resources = 500;
            inventory.items.add(ItemDbo.builder(inventory, ItemType.ElementGreen).withQuantity(3).build());
            inventory.items.add(ItemDbo.builder(inventory, ItemType.ElementOrange).withQuantity(1).build());
            inventory.update(connection);
            connection.commit();

            // Verify update
            InventoryDbo inventoryAltered = inventoryFactory.getByPlayerId(connection, player.getId());
            assertNotNull(inventoryAltered);
            assertEquals(inventory.toJsonObject(), inventoryAltered.toJsonObject());

            // Clear inventory
            inventory.clear();
            inventory.update(connection);
            connection.commit();
        }
    }
}
