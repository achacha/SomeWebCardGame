package org.achacha.webcardgame.game.tick;

import org.achacha.webcardgame.game.dbo.NexusDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;

/**
 * Central base of operations
 * Generates energy per tick
 * Processes resources into materials
 * Constructs
 */
public class Nexus implements Tickable {
    private final PlayerDbo owner;
    private final NexusDbo nexus;

    /**
     * Construct nexus for player
     * @param owner
     */
    public Nexus(PlayerDbo owner, NexusDbo nexus) {
        this.owner = owner;
        this.nexus = nexus;
    }

    @Override
    public void tick() {
        doGenerateEnergy();
        doProcessResources();
    }

    private void doGenerateEnergy() {
        // TODO:
    }

    private void doProcessResources() {
        // TODO:
    }

}
