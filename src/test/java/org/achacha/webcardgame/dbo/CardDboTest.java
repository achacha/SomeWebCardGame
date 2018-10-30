package org.achacha.webcardgame.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.CardDboFactory;
import org.achacha.webcardgame.game.logic.CardType;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardDboTest extends BaseInitializedTest {
    private CardDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(CardDbo.class);

    @Test
    void testGetCardsForPlayer() {
        Collection<CardDbo> cards = factory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(cards);
        assertEquals(5, cards.size());

        CardDbo one = cards.iterator().next();
        assertEquals(1, one.getId());
        assertEquals(TestDataConstants.JUNIT_PLAYER__ID, one.getPlayerId());
        assertEquals("Card 1", one.getName());
        assertEquals(8, one.getLevel());
        assertEquals(99990, one.getXp());
        assertEquals(30, one.getStrength());
        assertEquals(70, one.getAgility());
        assertEquals(CardType.Human, one.getType());
        assertNotNull(one.getStickers());
    }
}
