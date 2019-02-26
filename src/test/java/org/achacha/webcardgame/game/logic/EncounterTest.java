package org.achacha.webcardgame.game.logic;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.sticker.CardSticker;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncounterTest extends BaseInitializedTest {
    @Test
    void testEncounterCombat() throws SQLException {
        PlayerDbo player = createNewTestPlayer("test_encounter_combat");
        player.getInventory().setEnergy(100);

        player.getCards().add(
                CardDbo.builder(player.getId())
                        .withTypeAndRandomName(CardType.Human)
                        .withLevel(1)
                        .withStrength(12)
                        .withAgility(12)
                        .withDamage(12)
                        .withSticker(CardSticker.Type.HOT_AT5)
                        .withSticker(CardSticker.Type.DOT_AT10)
                        .build()
        );

        // TODO: Encounter contained in adventure should get a builder from adventure?
        // Encounter
        AdventureDbo adventure = AdventureDbo
                .builder(player.getId())
                .build();
        adventure.getEncounters().add(
                EncounterDbo
                        .builder(adventure)
                        .withCard(
                            CardDbo.builder(player.getId())
                                    .withType(CardType.Goblin)
                                    .withName("Bobo")
                                    .withLevel(1)
                                    .withStrength(8)
                                    .withAgility(15)
                                    .withDamage(9)
                                    .withXp(1000)
                                    .withSticker(CardSticker.Type.HOT_AT1)
                                    .withSticker(CardSticker.Type.DOT_AT3)
                                    .build()
                        )
                        .build()
        );

        EncounterProcessor processor = new EncounterProcessor(player, adventure, adventure.getEncounters().get(0));
        EncounterProcessor.Result result = processor.doEncounter();
        assertNotSame(result, EncounterProcessor.Result.None);  // None means the encounter started and has not finished
        assertTrue(processor.getEventLog().getEvents().size() > 0);
        System.out.println(processor.getEventLog().toString());
    }
}
