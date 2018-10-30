package org.achacha.webcardgame.game.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.logic.CardType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncounterDboTest extends BaseInitializedTest {

    @Test
    void builder() {
        EncounterDbo encounter = EncounterDbo.builder(CardType.Elf, 3, 75).build();
        assertEquals(3, encounter.getEnemies().size());
        assertEquals(3, encounter.getEnemies().stream().filter(enemy-> enemy.getLevel() == 75).count());
    }
}