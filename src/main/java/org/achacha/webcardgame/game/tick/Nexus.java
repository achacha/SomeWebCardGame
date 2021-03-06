package org.achacha.webcardgame.game.tick;

import com.google.common.base.Preconditions;
import org.achacha.webcardgame.game.dbo.NexusDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
    private final NexusDbo dbo;

    static final int RESOURCE_PROCESSING_PLAYER = 50;
    static final int RESOURCE_PROCESSING_NEXUS_1 = 100;
    static final int RESOURCE_PROCESSING_NEXUS_2 = 200;
    static final int RESOURCE_PROCESSING_NEXUS_3 = 300;

    // Energy gathering constants
    static final int ENERGY_GENERATION_PLAYER = 100;
    static final int ENERGY_GENERATION_NEXUS_1 = 200;
    static final int ENERGY_GENERATION_NEXUS_2 = 500;
    static final int ENERGY_GENERATION_NEXUS_3 = 1000;

    // Material processing constants


    /**
     * Construct dbo for player
     * @param playerDbo PlayerDbo
     * @param nexusDbo NexusDbo or null if none
     */
    public Nexus(PlayerDbo playerDbo, @Nullable NexusDbo nexusDbo) {
        Preconditions.checkState(nexusDbo == null || nexusDbo.getPlayerId() == playerDbo.getId());
        this.player = playerDbo;
        this.dbo = nexusDbo;
    }

    public NexusDbo getDbo() {
        return dbo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("player", player)
                .append("dbo", dbo)
                .toString();
    }

    @Override
    public void tick() {
        LOGGER.debug("+++Tick start for this={}", this);
        doGenerateEnergy();
        doProcessResources();
        LOGGER.debug("---Tick start for this={}", this);
    }

    private void doGenerateEnergy() {
        long energyToAdd = ENERGY_GENERATION_PLAYER;  // Base generated by the player

        // Additional generated by nexus if any
        if (dbo != null) {
            switch(dbo.getEnergyGathererType()) {
                case 1:
                    energyToAdd += ENERGY_GENERATION_NEXUS_1;
                    break;

                case 2:
                    energyToAdd += ENERGY_GENERATION_NEXUS_2;
                    break;

                case 3:
                    energyToAdd += ENERGY_GENERATION_NEXUS_3;
                    break;

                case 0:
                    // Only player, no generator on nexus yet
                    break;

                default: throw new RuntimeException("Unexpected energy generation type=" + dbo.getEnergyGathererType());
            }
        }

        LOGGER.debug("Adding energy {} to this={}", energyToAdd, this);
        player.getInventory().addEnergy(energyToAdd);

    }

    private void doProcessResources() {
        long resources = player.getInventory().getResources();

        long resourcesToProcess = Math.min(resources, RESOURCE_PROCESSING_PLAYER);  // Base player processing

        if (dbo != null) {
            // Nexus processing
            switch(dbo.getResourceProcessingType()) {
                case 1:
                    resourcesToProcess += Math.min(resources, RESOURCE_PROCESSING_NEXUS_1);
                    break;

                case 2:
                    resourcesToProcess += Math.min(resources, RESOURCE_PROCESSING_NEXUS_2);
                    break;

                case 3:
                    resourcesToProcess += Math.min(resources, RESOURCE_PROCESSING_NEXUS_3);
                    break;

                case 0:
                    // Player only processing, nothing on nexus yet
                    break;
                default: throw new RuntimeException("Unexpected resource processor type=" + dbo.getResourceProcessingType());
            }
        }

        LOGGER.debug("Processing resources to materials {} to this={}", resourcesToProcess, this);
        player.getInventory().convertResourcesToMaterials(resourcesToProcess);
    }
}
