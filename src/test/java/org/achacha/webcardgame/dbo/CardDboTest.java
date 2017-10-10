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
        Assert.assertEquals(2, cards.size());
    }
}
