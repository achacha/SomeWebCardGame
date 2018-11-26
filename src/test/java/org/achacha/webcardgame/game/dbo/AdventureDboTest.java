package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.logic.EncounterProcessor;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdventureDboTest extends BaseInitializedTest {
    private AdventureDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(AdventureDbo.class);

    @Test
    void testInsert() throws Exception {
        // Create adventure
        AdventureDbo adventure = AdventureDbo.builder(TestDataConstants.JUNIT_PLAYER__ID).build();

        adventure.getEncounters().add(EncounterDbo.builder()
                .withEnemy(CardType.Human, 2)
                .withEnemy(CardType.Goblin, 1)
                .withEnemy(CardType.Goblin, 1)
                .build());

        adventure.getEncounters().add(EncounterDbo.builder()
                .withEnemy(CardType.Elf, 3)
                .withEnemy(CardType.Elf, 2)
                .build());

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            adventure.insert(connection);
            connection.commit();
        }

        AdventureDbo adventureLoaded = factory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(adventureLoaded);
        assertEquals(TestDataConstants.JUNIT_ADVENTURE_ID, adventureLoaded.playerId);
        assertEquals(adventure.toJsonObject().toString(), adventureLoaded.toJsonObject().toString());

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            factory.deleteById(connection, adventure.getId());
            connection.commit();
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

        AdventureDboFactory adventureFactory = dbm.getFactory(AdventureDbo.class);
        AdventureArchiveDboFactory adventureArchiveFactory = dbm.getFactory(AdventureArchiveDbo.class);

        try (Connection connection = dbm.getConnection()) {
            PlayerDbo player = dbm.<PlayerDboFactory>getFactory(PlayerDbo.class).getById(connection, TestDataConstants.JUNIT_PLAYER__ID);
            assertNotNull(player);

            AdventureDbo adventure = AdventureDbo.builder(player.getId()).build();
            adventure.getEncounters().add(EncounterDbo.builder()
                    .withEnemy(CardType.Goblin, 3)
                    .withEnemy(CardType.Goblin, 3)
                    .withEnemy(CardType.Human, 6)
                    .build());

            adventure.getEncounters().add(EncounterDbo.builder()
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
            adventureFactory.deleteById(connection, adventureId);
            connection.commit();

            // Verify move, original deleted
            AdventureDbo deletedAdventure = adventureFactory.getById(connection, adventureId);
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
}
