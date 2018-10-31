package org.achacha.webcardgame.game.logic;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.InventoryDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class EncounterTest extends BaseInitializedTest {
    @Test
    void testEncounterCombat() {
        // Player
        ArrayList<CardDbo> cards = new ArrayList<>();
        CardDbo card = new CardDbo();
        card.setLevel(1);
        card.setStrength(10);
        card.setAgility(10);
        cards.add(card);
        PlayerDbo player = new PlayerDbo();
        player.setEnergy(100);
        player.setCards(cards);
        InventoryDbo inventory = new InventoryDbo();
        inventory.setItems(new ArrayList<>());
        player.setInventory(inventory);
        System.out.println("player="+player);

        // Encounter
        EncounterDbo encounter = EncounterDbo.builder(CardType.Goblin, 1, 1).build();
        CardDbo enemy = encounter.getEnemies().get(0);
        enemy.setStrength(5);
        enemy.setAgility(15);
        enemy.setXp(1000);

        if (EncounterProcessor.process(player, encounter))
            System.out.println("Player wins!");
        else
            System.out.println("Player fails!");

        System.out.println("\nplayer="+player);
        System.out.println("encounter="+encounter);
    }
}
