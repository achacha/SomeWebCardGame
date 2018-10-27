package org.achacha.webcardgame.game.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.logic.EnemyType;
import org.junit.Assert;
import org.junit.Test;

public class EnemyCardDboTest extends BaseInitializedTest {

    @Test
    public void testBuilder() {
        EnemyCardDbo dbo = EnemyCardDbo.builder(EnemyType.Human, 50).build();
        Assert.assertEquals(50, dbo.getLevel());
        Assert.assertFalse(dbo.getName().isBlank());
    }
}