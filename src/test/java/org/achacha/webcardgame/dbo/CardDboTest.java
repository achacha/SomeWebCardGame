package org.achacha.webcardgame.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.CardDboFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class CardDboTest extends BaseInitializedTest {
    @Test
    public void testGetCardsForPlayer() {
        Collection<CardDbo> cards = CardDboFactory.getByPlayerId(TestDataConstants.JUNIT_PLAYERID);
        Assert.assertNotNull(cards);
        Assert.assertEquals(3, cards.size());

        CardDbo one = cards.iterator().next();
        Assert.assertEquals(1, one.getId());
        Assert.assertEquals(TestDataConstants.JUNIT_PLAYERID, one.getPlayerId());
        Assert.assertEquals("Card 1", one.getName());
        Assert.assertEquals(8, one.getLevel());
        Assert.assertEquals(99990, one.getXp());
        Assert.assertEquals(30, one.getStrength());
        Assert.assertEquals(70, one.getAgility());
        Assert.assertEquals(45, one.getStamina());
        Assert.assertNotNull(one.getStickers());
    }
}
