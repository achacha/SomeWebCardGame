package org.achacha.webcardgame.game.tick;

import com.google.common.base.Preconditions;
import org.achacha.webcardgame.game.dbo.NexusDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * Central base of operations
 * Generates energy per tick
 * Processes resources into materials
 * Constructs
 */
public class Nexus implements Tickable {
    transient private static final Logger LOGGER = LogManager.getLogger(Nexus.class);

    private final PlayerDbo player;
    private final NexusDbo nexus;

    private static final int BASE_RESOURCE_PROCESING = 50;

    // Energy gathering constants
    private static final int ENERGY_GENERATION_NEXUS_1 = 200;
    private static final int ENERGY_GENERATION_NEXUS_2 = 500;
    private static final int ENERGY_GENERATION_NEXUS_3 = 1000;

    // Material processing constants


    /**
     * Construct nexus for player
     * @param playerDbo PlayerDbo
     * @param nexusDbo NexusDbo or null if none
     */
    public Nexus(PlayerDbo playerDbo, @Nullable NexusDbo nexusDbo) {
        Preconditions.checkState(nexusDbo == null || nexusDbo.getPlayerId() == playerDbo.getId());
        this.player = playerDbo;
        this.nexus = nexusDbo;
    }

    public NexusDbo getNexus() {
        return nexus;
    }

    @Override
    public void tick() {
        LOGGER.debug("+++Tick start for nexus={}", this);
        doGenerateEnergy();
        doProcessResources();
        LOGGER.debug("---Tick start for nexus={}", this);
    }

    private void doGenerateEnergy() {
        // No nexus, player generates otherwise use nexus type
        if (nexus != null) {
            switch(nexus.getEnergyGathererType()) {
                case 1:
                    player.getInventory().addEnergy(ENERGY_GENERATION_NEXUS_1);
                    break;

                case 2:
                    player.getInventory().addEnergy(ENERGY_GENERATION_NEXUS_2);
                    break;

                case 3:
                    player.getInventory().addEnergy(ENERGY_GENERATION_NEXUS_3);
                    break;

                case 0: break;
                default: throw new RuntimeException("Unexpected energy generation type="+nexus.getEnergyGathererType());
            }
        }
    }

    private void doProcessResources() {
        long materials = player.getInventory().getMaterials();

        // TODO: Process player resources

        player.getInventory().setMaterials(materials);
    }
}
