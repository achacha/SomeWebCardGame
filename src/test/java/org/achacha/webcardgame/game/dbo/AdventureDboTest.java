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
        // Create adventure
        AdventureDbo adventure = AdventureDbo.builder(TestDataConstants.JUNIT_PLAYER__ID).build();

        adventure.getEncounters().add(EncounterDbo.builder(adventure)
                .withEnemy(CardType.Human, 2)
                .withEnemy(CardType.Goblin, 1)
                .withEnemy(CardType.Goblin, 1)
                .build());

        adventure.getEncounters().add(EncounterDbo.builder(adventure)
                .withEnemy(CardType.Elf, 3)
                .withEnemy(CardType.Elf, 2)
                .build());

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            adventure.insert(connection);
            connection.commit();

            AdventureDbo adventureLoaded = factory.getByPlayerId(connection, TestDataConstants.JUNIT_PLAYER__ID);
            assertNotNull(adventureLoaded);
            assertEquals(TestDataConstants.JUNIT_PLAYER__ID, adventureLoaded.playerId);
            assertEquals(adventure.toJsonObject().toString(), adventureLoaded.toJsonObject().toString());

            factory.deleteById(connection, adventure.getId());
            connection.commit();
        }
    }

    @Test
    void testUniqueActive() throws Exception {
        PlayerDbo player = createNewTestPlayer();
        AdventureDbo adventure1 = AdventureDbo.builder(player.getId()).build();
        adventure1.getEncounters().add(EncounterDbo.builder(adventure1)
                .withEnemy(CardType.Human, 1)
                .build());

        AdventureDbo adventure2 = AdventureDbo.builder(player.getId()).build();
        adventure2.getEncounters().add(EncounterDbo.builder(adventure2)
                .withEnemy(CardType.Human, 2)
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
            factory.deleteAllByPlayerId(connection, TestDataConstants.JUNIT_PLAYER__ID);
            assertNull(factory.getByPlayerId(connection, TestDataConstants.JUNIT_PLAYER__ID));
        }
    }

    @Test
    void builderEmpty() {
        AdventureDbo adventure = AdventureDbo.builder(TestDataConstants.JUNIT_PLAYER__ID).build();
        assertEquals(TestDataConstants.JUNIT_PLAYER__ID, adventure.playerId);
        assertNotNull(adventure.getEncounters());
        assertEquals(0, adventure.getEncounters().size());
    }

    @Test
    void persistence() throws Exception {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();

        try (Connection connection = dbm.getConnection()) {
            PlayerDbo player = createNewTestPlayer();
            assertNotNull(player);

            // TODO: Auto-add encounter to adventure
            AdventureDbo adventure = AdventureDbo.builder(player.getId()).build();
            adventure.getEncounters().add(EncounterDbo.builder(adventure)
                    .withEnemy(CardType.Goblin, 3)
                    .withEnemy(CardType.Goblin, 3)
                    .withEnemy(CardType.Human, 6)
                    .build());

            adventure.getEncounters().add(EncounterDbo.builder(adventure)
                    .withEnemy(CardType.Elf, 5)
                    .withEnemy(CardType.Elf, 7)
                    .build());

            adventure.insert(connection);
            Timestamp originalCreated = adventure.created;
            assertNotNull(originalCreated);
            connection.commit();

            // Simulate encounters
            adventure.getEncounters().forEach(encounter->{
                EncounterProcessor processor = new EncounterProcessor(player, encounter);
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
            assertEquals(adventureId, archivedAdventure.getOriginalId());
            assertEquals(originalCreated, archivedAdventure.getOriginalCreated());
            assertNotNull(archivedAdventure.getCompleted());
            assertNotNull(archivedAdventure.getEncounters());
            assertEquals(2, archivedAdventure.getEncounters().size());
            assertTrue(archivedAdventure.getEncounters().get(0).getEnemies().size() > 0);
            assertEquals(3, archivedAdventure.getEncounters().get(0).getEnemies().get(0).getLevel());
        }
    }

    @Test
    void testToFromJson() {
        AdventureDbo original = AdventureDbo.builder(TestDataConstants.JUNIT_PLAYER__ID).build();
        original.getEncounters().add(EncounterDbo.builder(original).withEnemy(CardType.Human, 3, "enemy_1").build());
        original.getEncounters().add(EncounterDbo.builder(original).withEnemy(CardType.Elf, 2, "enemy_2").build());
        String originalJson = original.toJsonObject().toString();
        assertEquals("{\"id\":0,\"playerId\":1,\"encounters\":[{\"id\":0,\"adventureId\":0,\"enemies\":[{\"id\":0,\"name\":\"enemy_1\",\"type\":\"Human\",\"level\":3,\"xp\":0,\"health\":100,\"strength\":10,\"agility\":10,\"damage\":10,\"playerId\":0,\"encounterId\":0}],\"result\":\"None\"},{\"id\":0,\"adventureId\":0,\"enemies\":[{\"id\":0,\"name\":\"enemy_2\",\"type\":\"Elf\",\"level\":2,\"xp\":0,\"health\":100,\"strength\":10,\"agility\":10,\"damage\":10,\"playerId\":0,\"encounterId\":0}],\"result\":\"None\"}]}", originalJson);

        AdventureDbo restored = Global.getInstance().getGson().fromJson(originalJson, AdventureDbo.class);
        assertEquals(originalJson, restored.toJsonObject().toString());
    }
}
