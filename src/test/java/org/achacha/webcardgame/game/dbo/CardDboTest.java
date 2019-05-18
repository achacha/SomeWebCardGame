package org.achacha.webcardgame.game.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.sticker.CardSticker;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import static org.achacha.test.TestHelper.createNewTestPlayer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardDboTest extends BaseInitializedTest {
    private CardDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(CardDbo.class);

    @Test
    void testLoadCardsForKnownPlayer() throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            Collection<CardDbo> cards = factory.getByPlayerId(connection, TestDataConstants.JUNIT_PLAYER__ID1);
            assertNotNull(cards);
            assertEquals(5, cards.size());

            CardDbo one = cards.iterator().next();
            assertEquals(1, one.getId());
            assertEquals(TestDataConstants.JUNIT_PLAYER__ID1, one.getPlayerId());
            assertEquals("Card 1", one.getName());
            assertEquals(8, one.getLevel());
            assertEquals(99990, one.getXp());
            assertEquals(12, one.getStrength());   // Default
            assertEquals(10, one.getAgility());
            assertEquals(CardType.Human, one.getType());
            assertNotNull(one.getStickers());
        }
    }

    @Test
    void cardStickerNameConsistency() throws SQLException {
        PlayerDbo player = createNewTestPlayer("test_card_sticker_consistency");
        CardDbo card = CardDbo.builder(player.getId())
                .withTypeAndRandomName(CardType.Human)
                .withLevel(4)
                .withSticker(CardSticker.Type.HOT_AT3)
                .withSticker(CardSticker.Type.DOT_AT10)
                .build();

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            card.insert(connection);
            connection.commit();

            CardDbo cardLoaded = Global.getInstance().getDatabaseManager().<CardDboFactory>getFactory(CardDbo.class).getById(connection, card.getId());
            assertNotNull(cardLoaded);
            assertEquals(card.toJsonObject().toString(), cardLoaded.toJsonObject().toString());
        }
    }

    @Test
    void testDeleteRemovedCards() throws SQLException {
        PlayerDbo player = createNewTestPlayer("test_card_delete_remove");
        int originalCount = player.getCards().size();

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            // This should insert the cards
            player.update(connection);
            connection.commit();

            // Verify it saved
            PlayerDbo loadedPlayer = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getById(connection, player.getId());
            assertNotNull(loadedPlayer);
            assertEquals(originalCount, loadedPlayer.getCards().size());

            // Remove card, add new card and update
            CardDbo removedCard = player.getCards().remove(0);
            assertEquals(originalCount-1, player.getCards().size());

            player.getCards().add(CardDbo.builder(player.getId())
                    .withTypeAndRandomName(CardType.Human)
                    .withLevel(6)
                    .build()
            );
            player.update(connection);
            connection.commit();

            PlayerDbo loadedPlayerAgain = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getById(connection, player.getId());
            assertNotNull(loadedPlayerAgain);
            assertEquals(originalCount, loadedPlayerAgain.getCards().size());
            assertFalse(loadedPlayerAgain.getCards().stream().map(CardDbo::getId).anyMatch(id-> id == removedCard.getId()));
        }
    }
}
