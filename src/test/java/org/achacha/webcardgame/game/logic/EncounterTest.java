package org.achacha.webcardgame.game.logic;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.InventoryDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncounterTest extends BaseInitializedTest {
    @Test
    void testEncounterCombat() {
        // Player
        ArrayList<CardDbo> cards = new ArrayList<>();
        CardDbo card = new CardDbo();
        card.setLevel(1);
        card.setStrength(12);
        card.setAgility(10);
        card.setDamage(12);
        cards.add(card);
        PlayerDbo player = new PlayerDbo();
        player.setEnergy(100);
        player.setCards(cards);
        InventoryDbo inventory = new InventoryDbo();
        inventory.setItems(new ArrayList<>());
        player.setInventory(inventory);

        // Encounter
        EncounterDbo encounter = EncounterDbo.builder(CardType.Goblin, 1, 1).build();
        CardDbo enemy = encounter.getEnemies().get(0);
        enemy.setStrength(8);
        enemy.setAgility(15);
        enemy.setDamage(9);
        enemy.setXp(1000);

        EncounterProcessor processor = new EncounterProcessor(player, encounter);
        EncounterProcessor.Result result = processor.doEncounter();
        assertNotSame(result, EncounterProcessor.Result.None);  // None means the encounter started and has not finished
        assertTrue(processor.getEventLog().getEvents().size() > 0);
    }
}
