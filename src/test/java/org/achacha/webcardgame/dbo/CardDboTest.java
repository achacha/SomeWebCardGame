package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.CardDboFactory;
import org.achacha.webcardgame.game.logic.CardType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class CardDboTest extends BaseInitializedTest {
    private CardDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(CardDbo.class);

    @Test
    public void testGetCardsForPlayer() {
        Collection<CardDbo> cards = factory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);
        Assert.assertNotNull(cards);
        Assert.assertEquals(5, cards.size());

        CardDbo one = cards.iterator().next();
        Assert.assertEquals(1, one.getId());
        Assert.assertEquals(TestDataConstants.JUNIT_PLAYER__ID, one.getPlayerId());
        Assert.assertEquals("Card 1", one.getName());
        Assert.assertEquals(8, one.getLevel());
        Assert.assertEquals(99990, one.getXp());
        Assert.assertEquals(30, one.getStrength());
        Assert.assertEquals(70, one.getAgility());
        Assert.assertEquals(CardType.Human, one.getType());
        Assert.assertNotNull(one.getStickers());
    }
}
