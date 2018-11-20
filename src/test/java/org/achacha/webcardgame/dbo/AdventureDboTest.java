package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AdventureDboTest extends BaseInitializedTest {
    private AdventureDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(AdventureDbo.class);

    @Test
    void testGetItemsForInventory() {
        AdventureDbo adventure = factory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(adventure);
        assertEquals(TestDataConstants.JUNIT_ADVENTURE_ID, adventure.getId());

        assertNotNull(adventure.getEncounters());
        assertEquals(1, adventure.getEncounters().size());
        assertFalse(adventure.getEncounters().isEmpty());

        EncounterDbo encounter = adventure.getEncounters().get(0);
        assertNotNull(encounter);
        assertEquals(adventure.getId(), encounter.getAdventureId());
        assertFalse(encounter.getEnemies().isEmpty());

        CardDbo enemyCardDbo = encounter.getEnemies().get(0);
        assertNotNull(enemyCardDbo);
        assertEquals(encounter.getId(), enemyCardDbo.getEncounterId());
        assertEquals(8, enemyCardDbo.getLevel());
        assertEquals(100, enemyCardDbo.getXp());
        assertEquals(30, enemyCardDbo.getStrength());
        assertEquals(70, enemyCardDbo.getAgility());
        assertEquals("Enemy 1", enemyCardDbo.getName());
        assertNotNull(enemyCardDbo.getStickers());
        assertEquals(3, enemyCardDbo.getStickers().size());

        enemyCardDbo = encounter.getEnemies().get(1);
        assertNotNull(enemyCardDbo);
        assertEquals(encounter.getId(), enemyCardDbo.getEncounterId());
        assertEquals(6, enemyCardDbo.getLevel());
        assertEquals(80, enemyCardDbo.getXp());
        assertEquals(65, enemyCardDbo.getStrength());
        assertEquals(65, enemyCardDbo.getAgility());
        assertEquals("Enemy 2", enemyCardDbo.getName());
        assertNotNull(enemyCardDbo.getStickers());
        assertEquals(2, enemyCardDbo.getStickers().size());
    }

    @Test
    void builder() {
        AdventureDbo adventure = AdventureDbo.builder(2, 100).build();
        assertEquals(2, adventure.getEncounters().size());
    }
}
