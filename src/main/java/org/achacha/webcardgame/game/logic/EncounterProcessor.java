package org.achacha.webcardgame.game.logic;

import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class EncounterProcessor {
    private static final Logger LOGGER = LogManager.getLogger(EncounterProcessor.class);

    /**
     *
     * @param player PlayerDbo
     * @param encounter EncounterDbo
     * @return true if player wins
     */
    public static boolean process(PlayerDbo player, EncounterDbo encounter) {
        Optional<CardDbo> playerCard1 = getNextPlayerCard(player);
        Optional<CardDbo> enemyCard1 = getNextEnemyCard(encounter);

        while (playerCard1.isPresent() && enemyCard1.isPresent() && playerCard1.get().getHealth() > 0 && enemyCard1.get().getHealth() > 0){
            CardDbo playerCard = playerCard1.get();
            CardDbo enemyCard = enemyCard1.get();

            // Battle until one is dead
            int i = 0;
            while (playerCard.getHealth() > 0 && enemyCard.getHealth() > 0 && i++ < 100) {
                // Combat
                int initiativeBonus = (i % 2 == 0 ? RandomUtils.nextInt(0, 10) : -RandomUtils.nextInt(0, 10));
                int playerDamage = initiativeBonus;
                playerDamage += playerCard.getLevel() + 2 * playerCard.getStrength();
                playerDamage -= enemyCard.getLevel() + enemyCard.getAgility();
                playerDamage += RandomUtils.nextInt(0, playerCard.getLevel());
                if (playerDamage < 0)
                    playerDamage = 0;

                int enemyDamage = -initiativeBonus;
                enemyDamage += enemyCard.getLevel() + 2 * enemyCard.getStrength();
                enemyDamage -= playerCard.getLevel() + playerCard.getAgility();
                enemyDamage += RandomUtils.nextInt(0, enemyCard.getLevel());
                if (enemyDamage < 0)
                    enemyDamage = 0;

                LOGGER.debug("Combat[{}]: playerDamage={} playerHealth={} enemyDamage={} enemyHealth={}", i, playerDamage, playerCard.getHealth(), enemyDamage, enemyCard.getHealth());

                playerCard.decHealth(enemyDamage);
                enemyCard.decHealth(playerDamage);
            }
            if (i >= 100) {
                LOGGER.error("Failed to finish encounter after 100 moves, enemy dies.");
                enemyCard.setHealth(0);
            }

            playerCard1 = getNextPlayerCard(player);
            enemyCard1 = getNextEnemyCard(encounter);

        }

        return playerCard1.isPresent() && playerCard1.get().getHealth() > 0;
    }

    private static Optional<CardDbo> getNextEnemyCard(EncounterDbo encounter) {
        return encounter.getEnemies().stream().filter(CardDbo::isAlive).findFirst();
    }

    private static Optional<CardDbo> getNextPlayerCard(PlayerDbo player) {
        return player.getCards().stream().filter(CardDbo::isAlive).findFirst();
    }


}
