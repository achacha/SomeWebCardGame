package org.achacha.webcardgame.game.tick;

import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tickable handler for PlayerDbo
 */
public class Player implements Tickable {
    transient private static final Logger LOGGER = LogManager.getLogger(Player.class);

    private static final long ENERGY_GENERATION_PLAYER = 50;

    private final PlayerDbo player;

    public Player(PlayerDbo player) {
        this.player = player;
    }

    public PlayerDbo getPlayer() {
        return player;
    }

    @Override
    public void tick() {
        LOGGER.debug("+++Tick start for player={}", this);
        player.getInventory().addEnergy(ENERGY_GENERATION_PLAYER);
        LOGGER.debug("---Tick end for player={}", this);
    }
}
