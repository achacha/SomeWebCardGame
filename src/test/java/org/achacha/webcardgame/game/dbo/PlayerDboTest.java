package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.data.ItemType;
import org.achacha.webcardgame.sticker.CardSticker;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerDboTest extends BaseInitializedTest {
    private PlayerDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(PlayerDbo.class);

    @Test
    void testGetPlayerForLogin() throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            List<PlayerDbo> players = factory.getByLoginId(connection, TestDataConstants.JUNIT_USER_LOGINID);
            assertNotNull(players);
            assertTrue(players.size() > 0);  // Concurrent tests may be running that create players

            PlayerDbo player = players.get(0);
            assertEquals(1200, player.getInventory().getEnergy());
            assertEquals(5000, player.getInventory().getMaterials());
            assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
        }
    }

    @Test
    void testGetPlayerForId() throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = factory.getByLoginIdAndPlayerId(connection, TestDataConstants.JUNIT_USER_LOGINID, TestDataConstants.JUNIT_PLAYER__ID);
            assertNotNull(player);
            assertEquals(1200, player.getInventory().getEnergy());
            assertEquals(5000, player.getInventory().getMaterials());
            assertEquals(TestDataConstants.JUNIT_PLAYER__ID, player.getId());
        }
    }

    @Test
    void testPlayerDeleteWithCascade() throws SQLException {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        PlayerDbo player = createNewTestPlayer();

        // Build inventory
        player.getInventory().energy = 100;
        player.getInventory().materials = 2400;
        player.getInventory().items.add(ItemDbo.builder(player.getInventory(), ItemType.ElementGreen).withQuantity(3).build());
        player.getInventory().items.add(ItemDbo.builder(player.getInventory(), ItemType.ElementOrange).withQuantity(1).build());


        // Build cards
        player.getCards().add(
                CardDbo.builder(player.getId())
                        .withTypeAndRandomName(CardType.Elf)
                        .withLevel(2)
                        .withDamage(12)
                        .withSticker(CardSticker.Type.DOT_AT1)
                        .withSticker(CardSticker.Type.HOT_AT1)
                        .build()

        );

        try (Connection connection = dbm.getConnection()) {
            // Persist
            player.update(connection);
            connection.commit();

            long playerId = player.getId();
            long inventoryId = player.getInventory().getId();

            // Check that we persisted the player, inventory, items, cards, etc
            assertNotNull(dbm.getFactory(PlayerDbo.class).getById(connection, playerId));
            assertNotNull(dbm.<InventoryDboFactory>getFactory(InventoryDbo.class).getByPlayerId(connection, playerId));
            assertFalse(dbm.<ItemDboFactory>getFactory(ItemDbo.class).getByInventoryId(connection, inventoryId).isEmpty());
            assertFalse(dbm.<CardDboFactory>getFactory(CardDbo.class).getByPlayerId(connection, playerId).isEmpty());

            // Save ids and then delete player and all related objects
            assertTrue(playerId > 0);
            assertTrue(inventoryId > 0);

            // Delete should cascade
            dbm.<PlayerDboFactory>getFactory(PlayerDbo.class).deleteById(connection, playerId);
            connection.commit();

            // Check that we deleted the player, inventory, items, cards, etc
            assertNull(dbm.getFactory(PlayerDbo.class).getById(connection, playerId));
            assertNull(dbm.<InventoryDboFactory>getFactory(InventoryDbo.class).getByPlayerId(connection, playerId));
            assertTrue(dbm.<ItemDboFactory>getFactory(ItemDbo.class).getByInventoryId(connection, inventoryId).isEmpty());
            assertTrue(dbm.<CardDboFactory>getFactory(CardDbo.class).getByPlayerId(connection, playerId).isEmpty());
        }
    }
}
