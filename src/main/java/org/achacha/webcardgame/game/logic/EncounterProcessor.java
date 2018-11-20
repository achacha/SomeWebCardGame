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

    private final PlayerDbo player;
    private final EncounterDbo encounter;

    private EncounterEventLog eventLog;

    public enum Result {
        None,
        Win,
        Draw,
        Lose
    }

    private Result result = Result.None;

    public EncounterProcessor(PlayerDbo player, EncounterDbo encounter) {
        this.player = player;
        this.encounter = encounter;
        eventLog = new EncounterEventLog(player, encounter);
    }

    public EncounterEventLog getEventLog() {
        return eventLog;
    }

    public Result getResult() {
        return result;
    }

    /**
     * Process encounter
     * @return Encounter result
     */
    public Result doEncounter() {
        eventLog.add(EncounterEvent.builder(EventType.Start, false).withId(encounter.getId()).build());
        eventLog.add(EncounterEvent.builder(EventType.Start).withId(player.getId()).build());
        Optional<CardDbo> playerCard1 = getNextPlayerCard();
        Optional<CardDbo> enemyCard1 = getNextEnemyCard();

        while (playerCard1.isPresent() && enemyCard1.isPresent() && playerCard1.get().getHealth() > 0 && enemyCard1.get().getHealth() > 0){
            CardDbo playerCard = playerCard1.get();
            eventLog.add(EncounterEvent.builder(EventType.CardStart).withCard(playerCard).build());

            CardDbo enemyCard = enemyCard1.get();
            eventLog.add(EncounterEvent.builder(EventType.CardStart, false).withCard(enemyCard).build());

            // Process before encounter sticker actions
            playerCard.processStickersBeforeEncounter(eventLog, enemyCard);
            enemyCard.processStickersBeforeEncounter(eventLog, playerCard);

            // Battle until one is dead
            int i = 0;
            boolean playerHasInitiative = true;
            while (playerCard.getHealth() > 0 && enemyCard.getHealth() > 0 && i++ < 100) {
                // Process before turn sticker actions
                playerCard.processStickersBeforeTurn(eventLog, enemyCard);
                enemyCard.processStickersBeforeTurn(eventLog, playerCard);

                // Combat
                DamagePerTurn playerDamage = calculateDamage(playerCard, enemyCard, playerHasInitiative);
                DamagePerTurn enemyDamage = calculateDamage(enemyCard, playerCard, !playerHasInitiative);
                LOGGER.debug("Combat[{},0]: playerDamage={} playerHealth={} enemyDamage={} enemyHealth={} initiative={}", i, playerDamage, playerCard.getHealth(), enemyDamage, enemyCard.getHealth(), playerHasInitiative);

                playerCard.decHealth(enemyDamage.damage);
                eventLog.add(playerDamage);
                enemyCard.decHealth(playerDamage.damage);
                eventLog.add(enemyDamage);
                LOGGER.debug("Combat[{},1]: playerDamage={} playerHealth={} enemyDamage={} enemyHealth={} initiative={}", i, playerDamage, playerCard.getHealth(), enemyDamage, enemyCard.getHealth(), playerHasInitiative);

                // Process after turn sticker actions
                playerCard.processStickersAfterTurn(eventLog, enemyCard);
                enemyCard.processStickersAfterTurn(eventLog, playerCard);

                // Current health update
                eventLog.add(EncounterEvent.builder(EventType.CardHealth).withValue(playerCard.getHealth()).build());
                eventLog.add(EncounterEvent.builder(EventType.CardHealth, false).withValue(enemyCard.getHealth()).build());

                // Switch initiative
                playerHasInitiative = !playerHasInitiative;
            }

            // Process after encounter sticker actions
            if (i >= 100) {
                LOGGER.error("Failed to finish encounter after 100 moves, both die");
                playerCard.setHealth(0);
                enemyCard.setHealth(0);
            }

            if (!playerCard.isAlive()) {
                eventLog.add(EncounterEvent.builder(EventType.CardDeath).withValue(playerCard.getHealth()).withId(playerCard.getId()).build());
                if (enemyCard.isAlive())
                    enemyCard.processStickersAfterEncounter(eventLog, playerCard);
            }

            if (!enemyCard.isAlive()) {
                eventLog.add(EncounterEvent.builder(EventType.CardDeath, false).withValue(enemyCard.getHealth()).withId(enemyCard.getId()).build());
                if (playerCard.isAlive())
                    playerCard.processStickersAfterEncounter(eventLog, enemyCard);
            }

            playerCard1 = getNextPlayerCard();
            enemyCard1 = getNextEnemyCard();
        }

        int resultValue = ((playerCard1.isPresent() && playerCard1.get().getHealth() > 0) ? 1 : 0) - ((enemyCard1.isPresent() && enemyCard1.get().getHealth() > 0) ? 1 : 0);
        if (resultValue > 0) {
            eventLog.add(EncounterEvent.builder(EventType.PlayerWin).build());
            result = Result.Win;
        }
        else if (resultValue < 0) {
            eventLog.add(EncounterEvent.builder(EventType.PlayerLose).build());
            result = Result.Lose;
        }
        else {
            eventLog.add(EncounterEvent.builder(EventType.PlayerDraw).build());
            result = Result.Draw;
        }
        return result;
    }

    private DamagePerTurn calculateDamage(CardDbo attacker, CardDbo defender, boolean isPlayerAttacking) {
        // if strength is higher than agility it will add bonus damage
        DamagePerTurn dpt = new DamagePerTurn(attacker.getDamage());
        dpt.setBit(DamagePerTurn.BitOffset.PLAYER_ATTACKING);

        LOGGER.debug("-calc[0]: dpt={}", dpt);
        dpt.damage += (1 + max(0, attacker.getStrength() - defender.getAgility())) * calculateLevelDiff(attacker.getLevel(), defender.getLevel());

        LOGGER.debug("-calc[1]: dpt={}", dpt);

        // Extra initiative damage
        if (isPlayerAttacking) {
            int levelDiff = attacker.getLevel() - defender.getLevel();
            dpt.damage += levelDiff <= 0 ? 1 : RandomUtils.nextInt(1, levelDiff * 2);
        }
        LOGGER.debug("-calc[2]: dpt={}", dpt);

        // Check for critical
        if (RandomUtils.nextInt(0,100) < attacker.getAgility()) {
            dpt.damage *= 2;
            dpt.setBit(DamagePerTurn.BitOffset.CRITICAL);
        }
        LOGGER.debug("-calc[3]: dpt={}", dpt);

        // Check for absorption
        if (RandomUtils.nextInt(0,100) < attacker.getStrength()) {
            dpt.damage /= 2;
            dpt.setBit(DamagePerTurn.BitOffset.ABSORB);
        }
        LOGGER.debug("-calc[4]: dpt={}", dpt);

        Preconditions.checkState(dpt.damage > 0);
        return dpt;
    }

    private int calculateLevelDiff(int attackerLevel, int defenderLevel) {
        int diff = max(1, attackerLevel - defenderLevel);
        return diff * diff;
    }

    private Optional<CardDbo> getNextEnemyCard() {
        return encounter.getEnemies().stream().filter(CardDbo::isAlive).findFirst();
    }

    private Optional<CardDbo> getNextPlayerCard() {
        return player.getCards().stream().filter(CardDbo::isAlive).findFirst();
    }
}
