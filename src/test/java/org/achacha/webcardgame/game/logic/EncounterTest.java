package org.achacha.webcardgame.game.logic;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.sticker.CardSticker;
import org.achacha.webcardgame.sticker.CardStickerFactory;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncounterTest extends BaseInitializedTest {
    @Test
    void testEncounterCombat() throws SQLException {
        // TODO: Builder extended withCards()
        PlayerDbo player = createNewTestPlayer();
        player.getInventory().setEnergy(100);

        CardDbo card = new CardDbo();
        card.setLevel(1);
        card.setStrength(12);
        card.setAgility(10);
        card.setDamage(12);
        card.setStickers(
                CardStickerFactory.builder()
                        .add(CardSticker.Type.HOT_AT5)
                        .add(CardSticker.Type.DOT_AT10)
                        .build()
        );
        player.getCards().add(card);

        // Encounter
        AdventureDbo adventure = new AdventureDbo();
        EncounterDbo encounter = EncounterDbo.builder(adventure).withEnemy(CardType.Goblin, 1).build();
        CardDbo enemyCard = encounter.getEnemies().get(0);
        enemyCard.setStrength(8);
        enemyCard.setAgility(15);
        enemyCard.setDamage(9);
        enemyCard.setXp(1000);
        enemyCard.setStickers(
                CardStickerFactory.builder()
                        .add(CardSticker.Type.HOT_AT1)
                        .add(CardSticker.Type.DOT_AT3)
                        .build()
        );

        EncounterProcessor processor = new EncounterProcessor(player, encounter);
        EncounterProcessor.Result result = processor.doEncounter();
        assertNotSame(result, EncounterProcessor.Result.None);  // None means the encounter started and has not finished
        assertTrue(processor.getEventLog().getEvents().size() > 0);
        System.out.println(processor.getEventLog().toString());
    }
}
