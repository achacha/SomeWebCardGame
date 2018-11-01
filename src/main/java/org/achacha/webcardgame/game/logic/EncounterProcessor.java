package org.achacha.webcardgame.game.logic;

import com.google.common.base.Preconditions;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static java.lang.Math.max;

public class EncounterProcessor {
    private static final Logger LOGGER = LogManager.getLogger(EncounterProcessor.class);

    /**
     * Process encounter
     * @param player PlayerDbo
     * @param encounter EncounterDbo
     * @return true if player wins
     */
    public boolean process(PlayerDbo player, EncounterDbo encounter) {
        Optional<CardDbo> playerCard1 = getNextPlayerCard(player);
        Optional<CardDbo> enemyCard1 = getNextEnemyCard(encounter);

        while (playerCard1.isPresent() && enemyCard1.isPresent() && playerCard1.get().getHealth() > 0 && enemyCard1.get().getHealth() > 0){
            CardDbo playerCard = playerCard1.get();
            CardDbo enemyCard = enemyCard1.get();

            // Battle until one is dead
            int i = 0;
            boolean playerHasInitiative = true;
            while (playerCard.getHealth() > 0 && enemyCard.getHealth() > 0 && i++ < 100) {
                // Combat
                DamagePerTurn playerDamage = calculateDamage(playerCard, enemyCard, playerHasInitiative);
                DamagePerTurn enemyDamage = calculateDamage(enemyCard, playerCard, !playerHasInitiative);
                LOGGER.debug("Combat[{},0]: playerDamage={} playerHealth={} enemyDamage={} enemyHealth={} initiative={}", i, playerDamage, playerCard.getHealth(), enemyDamage, enemyCard.getHealth(), playerHasInitiative);

                playerCard.decHealth(enemyDamage.damage);
                enemyCard.decHealth(playerDamage.damage);
                LOGGER.debug("Combat[{},1]: playerDamage={} playerHealth={} enemyDamage={} enemyHealth={} initiative={}", i, playerDamage, playerCard.getHealth(), enemyDamage, enemyCard.getHealth(), playerHasInitiative);

                // Switch initiative
                playerHasInitiative = !playerHasInitiative;
            }
            if (i >= 100) {
                LOGGER.error("Failed to finish encounter after 100 moves, both die");
                playerCard.setHealth(0);
                enemyCard.setHealth(0);
            }

            playerCard1 = getNextPlayerCard(player);
            enemyCard1 = getNextEnemyCard(encounter);
        }

        return playerCard1.isPresent() && playerCard1.get().getHealth() > 0;
    }

    private class DamagePerTurn {
        int damage;
        boolean isCritical;
        boolean isAbsorbed;

        DamagePerTurn(int damage) {
            this.damage = damage;
        }

        @Override
        public String toString() {
            return "{damage="+damage
                    +(isCritical ? ",CRIT" : "")
                    +(isAbsorbed ? ",ABS" : "")
                    +"}";
        }
    }

    private DamagePerTurn calculateDamage(CardDbo attacker, CardDbo defender, boolean attackerHasInitiative) {
        // if strength is higher than agility it will add bonus damage
        DamagePerTurn dpt = new DamagePerTurn(attacker.getDamage());
        LOGGER.debug("-calc[0]: dpt={}", dpt);
        dpt.damage += (1 + max(0, attacker.getStrength() - defender.getAgility())) * calculateLevelDiff(attacker.getLevel(), defender.getLevel());

        LOGGER.debug("-calc[1]: dpt={}", dpt);

        // Extra initiative damage
        if (attackerHasInitiative) {
            int levelDiff = attacker.getLevel() - defender.getLevel();
            dpt.damage += levelDiff <= 0 ? 1 : RandomUtils.nextInt(1, levelDiff * 2);
        }
        LOGGER.debug("-calc[2]: dpt={}", dpt);

        // Check for critical
        if (RandomUtils.nextInt(0,100) < attacker.getAgility()) {
            dpt.damage *= 2;
            dpt.isCritical = true;
        }
        LOGGER.debug("-calc[3]: dpt={}", dpt);

        // Check for absorption
        if (RandomUtils.nextInt(0,100) < attacker.getStrength()) {
            dpt.damage /= 2;
            dpt.isAbsorbed = true;
        }
        LOGGER.debug("-calc[4]: dpt={}", dpt);

        Preconditions.checkState(dpt.damage > 0);
        return dpt;
    }

    private int calculateLevelDiff(int attackerLevel, int defenderLevel) {
        int diff = max(1, attackerLevel - defenderLevel);
        return diff * diff;
    }

    private static Optional<CardDbo> getNextEnemyCard(EncounterDbo encounter) {
        return encounter.getEnemies().stream().filter(CardDbo::isAlive).findFirst();
    }

    private static Optional<CardDbo> getNextPlayerCard(PlayerDbo player) {
        return player.getCards().stream().filter(CardDbo::isAlive).findFirst();
    }


}
