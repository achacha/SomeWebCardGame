package org.achacha.webcardgame.game.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.data.CardType;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncounterDboTest extends BaseInitializedTest {

    @Test
    void builder() {
        AdventureDbo adventure = new AdventureDbo();  // temp object
        EncounterDbo encounter = EncounterDbo.builder(adventure)
                .withGeneratedCard(CardType.Elf, 75)
                .withGeneratedCard(CardType.Elf, 75)
                .withGeneratedCard(CardType.Elf, 75)
                .build();
        assertEquals(3, encounter.getEnemies().size());
        assertEquals(3, encounter.getEnemies().stream().filter(enemy-> enemy.getLevel() == 75).count());
    }

    @Test
    void persistence() throws Exception {
        PlayerDbo player = createNewTestPlayer("test_encounter_persistence");

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            AdventureDbo adventure = AdventureDbo.builder(player.getId())
                    .withCard(player.cards.get(1))
                    .withCard(player.cards.get(0))
                    .build();
            adventure.insert(connection);

            // Insert encounter
            EncounterDbo encounter = EncounterDbo.builder(adventure).withGeneratedCard(CardType.Elf, 10).build();
            encounter.adventureId = adventure.getId();
            encounter.insert(connection);
            connection.commit();

            long insertedId = encounter.getId();
            assertTrue(insertedId > 0);

            // Read it back from database
            EncounterDbo encounterReadBack = Global.getInstance().getDatabaseManager().<EncounterDboFactory>getFactory(EncounterDbo.class).getById(connection, insertedId);
            assertNotNull(encounterReadBack);
            assertEquals(encounter.toString(), encounterReadBack.toString());   // toString is consistent, otherwise object ids are compared

            // Delete adventure which triggers delete on encounters
            Global.getInstance().getDatabaseManager().<AdventureDboFactory>getFactory(AdventureDbo.class).deleteById(connection, adventure.getId());
            connection.commit();

            // Read it back to verify it is deleted
            EncounterDbo encounterDeleted = Global.getInstance().getDatabaseManager().<EncounterDboFactory>getFactory(EncounterDbo.class).getById(connection, insertedId);
            assertNull(encounterDeleted);
            connection.commit();   // This commit should be done even if last call is only a read via select, it may have been a select with stored proc that triggered an update
        }
    }
}