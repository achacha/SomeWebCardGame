package org.achacha.webcardgame.game.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.logic.CardType;
import org.junit.Assert;
import org.junit.Test;

public class EncounterDboTest extends BaseInitializedTest {

    @Test
    public void builder() {
        EncounterDbo encounter = EncounterDbo.builder(CardType.Elf, 3, 75).build();
        Assert.assertEquals(3, encounter.getEnemies().size());
        Assert.assertEquals(3, encounter.getEnemies().stream().filter(enemy-> enemy.getLevel() == 75).count());
    }
}