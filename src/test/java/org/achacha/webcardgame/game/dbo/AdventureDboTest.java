package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.logic.EncounterProcessor;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.achacha.test.TestHelper.createNewTestPlayer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdventureDboTest extends BaseInitializedTest {
    private AdventureDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(AdventureDbo.class);
    private AdventureArchiveDboFactory adventureArchiveFactory = Global.getInstance().getDatabaseManager().getFactory(AdventureArchiveDbo.class);

    @Test
    void testInsert() throws Exception {
        PlayerDbo player = createNewTestPlayer("adventure_test_insert");

        // Create adventure
        AdventureDbo adventure = AdventureDbo.builder(player.getId())
                .withTitle("Some adventure")
                .withCard(player.cards.get(0))
                .withCard(player.cards.get(1))
                .withCard(player.cards.get(2))
                .build();

        adventure.getEncounters().add(EncounterDbo.builder(adventure)
                .withGeneratedCard(CardType.Human, 2)
                .withGeneratedCard(CardType.Goblin, 1)
                .withGeneratedCard(CardType.Goblin, 1)
                .build());

        adventure.getEncounters().add(EncounterDbo.builder(adventure)
                .withGeneratedCard(CardType.Elf, 3)
                .withGeneratedCard(CardType.Elf, 2)
                .build());

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            adventure.insert(connection);
            connection.commit();

            AdventureDbo adventureLoaded = factory.getByPlayerId(connection, player.getId());
            assertNotNull(adventureLoaded);
            assertEquals(adventure.getPlayerId(), adventureLoaded.playerId);
            assertEquals(adventure.title, adventureLoaded.title);
            assertEquals(adventure.toJsonObject().toString(), adventureLoaded.toJsonObject().toString());

            factory.deleteById(connection, adventure.getId());
            connection.commit();
        }
    }

    @Test
    void testUniqueActive() throws Exception {
        PlayerDbo player = createNewTestPlayer("test_adventure_unique_active");
        AdventureDbo adventure1 = AdventureDbo.builder(player.getId())
                .withCard(player.cards.get(0))
                .build();
        adventure1.getEncounters().add(EncounterDbo.builder(adventure1)
                .withGeneratedCard(CardType.Human, 1)
                .build());

        AdventureDbo adventure2 = AdventureDbo.builder(player.getId())
                .withCard(player.cards.get(0))
                .build();
        adventure2.getEncounters().add(EncounterDbo.builder(adventure2)
                .withGeneratedCard(CardType.Human, 2)
                .build());

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            adventure1.insert(connection);
            connection.commit();

            // This will throw duplicate new constrain since the playerId column is declared UNIQUE
            assertThrows(SQLException.class, ()-> {
                adventure2.insert(connection);
                connection.commit();
            });
        }

        // Cleanup (and test delete all code)
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            factory.deleteAllByPlayerId(connection, TestDataConstants.JUNIT_PLAYER__ID1);
            assertNull(factory.getByPlayerId(connection, TestDataConstants.JUNIT_PLAYER__ID1));
        }
    }

    @Test
    void builderEmpty() {
        AdventureDbo adventure = AdventureDbo.builder(TestDataConstants.JUNIT_PLAYER__ID1).build();
        assertEquals(TestDataConstants.JUNIT_PLAYER__ID1, adventure.playerId);
        assertNotNull(adventure.getEncounters());
        assertEquals(0, adventure.getEncounters().size());
    }

    @Test
    void persistence() throws Exception {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();

        try (Connection connection = dbm.getConnection()) {
            PlayerDbo player = createNewTestPlayer("test_adventure_persistence");
            assertNotNull(player);

            AdventureDbo adventure = AdventureDbo.builder(player.getId())
                    .withTitle("Not Quite The Quest For The Holy Grail")
                    .withCard(player.cards.get(1))
                    .withCard(player.cards.get(0))
                    .build();

            adventure.getEncounters().add(EncounterDbo.builder(adventure)
                    .withGeneratedCard(CardType.Goblin, 3)
                    .withGeneratedCard(CardType.Goblin, 3)
                    .withGeneratedCard(CardType.Human, 6)
                    .build());

            adventure.getEncounters().add(EncounterDbo.builder(adventure)
                    .withGeneratedCard(CardType.Elf, 5)
                    .withGeneratedCard(CardType.Elf, 7)
                    .build());

            adventure.insert(connection);
            Timestamp originalCreated = adventure.created;
            assertNotNull(originalCreated);
            connection.commit();

            // Simulate encounters
            adventure.getEncounters().forEach(encounter->{
                EncounterProcessor processor = new EncounterProcessor(player, adventure, encounter);
                processor.doEncounter();
            });

            // This will trigger encounter calls
            long adventureId = adventure.getId();
            AdventureArchiveDbo adventureArchive = new AdventureArchiveDbo(adventure);
            adventureArchive.insert(connection);
            factory.deleteById(connection, adventureId);
            connection.commit();

            // Verify move, original deleted
            AdventureDbo deletedAdventure = factory.getById(connection, adventureId);
            assertNull(deletedAdventure);

            // Verify move, archive consistent
            AdventureArchiveDbo archivedAdventure = adventureArchiveFactory.getByOriginalId(connection, adventureId);
            assertNotNull(archivedAdventure);
            assertEquals("Not Quite The Quest For The Holy Grail", archivedAdventure.getTitle());
            assertEquals(adventureId, archivedAdventure.getOriginalId());
            assertEquals(originalCreated, archivedAdventure.getOriginalCreated());
            assertNotNull(archivedAdventure.getCompleted());
            assertNotNull(archivedAdventure.getEncounters());
            assertEquals(adventure.getPlayerCards().size(), archivedAdventure.getPlayerCards().size());
            assertEquals(2, archivedAdventure.getEncounters().size());
            assertTrue(archivedAdventure.getEncounters().get(0).getEnemies().size() > 0);
            assertEquals(3, archivedAdventure.getEncounters().get(0).getEnemies().get(0).getLevel());
        }
    }

    @Test
    void testToFromJson() throws SQLException {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (Connection connection = dbm.getConnection()) {
            PlayerDbo player = dbm.<PlayerDboFactory>getFactory(PlayerDbo.class).getById(connection, TestDataConstants.JUNIT_PLAYER__ID1);
            AdventureDbo original = AdventureDbo.builder(player.getId())
                    .withCard(player.cards.get(1))
                    .withCard(player.cards.get(0))
                    .build();
            original.getEncounters().add(EncounterDbo.builder(original).withGeneratedCard(CardType.Human, 3, "enemy_1").build());
            original.getEncounters().add(EncounterDbo.builder(original).withGeneratedCard(CardType.Elf, 2, "enemy_2").build());

            // Due to random titles, we replace title with original for the check
            original.setTitle("Stroll around the block");
            String originalJson = original.toJsonObject().toString();
            assertEquals(
                    "{\"id\":0,\"playerId\":1,\"title\":\"Stroll around the block\",\"playerCards\":[{\"id\":2,\"playerId\":1,\"name\":\"Card #2\",\"type\":\"Elf\",\"level\":6,\"strength\":10,\"agility\":12,\"damage\":10,\"encounterId\":0,\"stickers\":[\"HOT_AT3\"],\"xp\":0,\"health\":100},{\"id\":1,\"playerId\":1,\"name\":\"Card 1\",\"type\":\"Human\",\"level\":8,\"strength\":12,\"agility\":10,\"damage\":10,\"encounterId\":0,\"stickers\":[\"NOP\",\"HOT_AT1\",\"HOT_AT5\"],\"xp\":99990,\"health\":100}],\"encounters\":[{\"id\":0,\"adventureId\":0,\"enemies\":[{\"id\":0,\"playerId\":1,\"name\":\"enemy_1\",\"type\":\"Human\",\"level\":3,\"strength\":10,\"agility\":10,\"damage\":10,\"encounterId\":0,\"stickers\":[],\"xp\":0,\"health\":100}],\"result\":\"None\"},{\"id\":0,\"adventureId\":0,\"enemies\":[{\"id\":0,\"playerId\":1,\"name\":\"enemy_2\",\"type\":\"Elf\",\"level\":2,\"strength\":10,\"agility\":10,\"damage\":10,\"encounterId\":0,\"stickers\":[],\"xp\":0,\"health\":100}],\"result\":\"None\"}]}",
                    originalJson
            );

            AdventureDbo restored = Global.getInstance().getGson().fromJson(originalJson, AdventureDbo.class);
            assertEquals(originalJson, restored.toJsonObject().toString());
        }
    }
}
