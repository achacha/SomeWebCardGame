package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.EnemyCardDbo;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AdventureDboTest extends BaseInitializedTest {
    private AdventureDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(AdventureDbo.class);

    @Test
    public void testGetItemsForInventory() {
        List<AdventureDbo> adventures = factory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);
        Assert.assertNotNull(adventures);
        Assert.assertEquals(1, adventures.size());

        AdventureDbo adventure = adventures.get(0);
        Assert.assertNotNull(adventure);
        Assert.assertEquals(TestDataConstants.JUNIT_ADVENTURE_ID, adventure.getId());

        Assert.assertNotNull(adventure.getEncounters());
        Assert.assertEquals(1, adventure.getEncounters().size());
        Assert.assertFalse(adventure.getEncounters().isEmpty());

        EncounterDbo encounter = adventure.getEncounters().get(0);
        Assert.assertNotNull(encounter);
        Assert.assertEquals(adventure.getId(), encounter.getAdventureId());
        Assert.assertFalse(encounter.getEnemies().isEmpty());

        EnemyCardDbo enemyCardDbo = encounter.getEnemies().get(0);
        Assert.assertNotNull(enemyCardDbo);
        Assert.assertEquals(encounter.getId(), enemyCardDbo.getEncounterId());
        Assert.assertEquals(8, enemyCardDbo.getLevel());
        Assert.assertEquals(100, enemyCardDbo.getXp());
        Assert.assertEquals(30, enemyCardDbo.getStrength());
        Assert.assertEquals(70, enemyCardDbo.getAgility());
        Assert.assertEquals(45, enemyCardDbo.getStamina());
        Assert.assertEquals("Enemy 1", enemyCardDbo.getName());
        Assert.assertNotNull(enemyCardDbo.getStickers());
        Assert.assertEquals(3, enemyCardDbo.getStickers().size());

        enemyCardDbo = encounter.getEnemies().get(1);
        Assert.assertNotNull(enemyCardDbo);
        Assert.assertEquals(encounter.getId(), enemyCardDbo.getEncounterId());
        Assert.assertEquals(6, enemyCardDbo.getLevel());
        Assert.assertEquals(80, enemyCardDbo.getXp());
        Assert.assertEquals(65, enemyCardDbo.getStrength());
        Assert.assertEquals(65, enemyCardDbo.getAgility());
        Assert.assertEquals(40, enemyCardDbo.getStamina());
        Assert.assertEquals("Enemy 2", enemyCardDbo.getName());
        Assert.assertNotNull(enemyCardDbo.getStickers());
        Assert.assertEquals(2, enemyCardDbo.getStickers().size());

    }
}
