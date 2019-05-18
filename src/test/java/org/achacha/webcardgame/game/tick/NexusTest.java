package org.achacha.webcardgame.game.tick;

import org.achacha.test.BaseInitializedTest;
import org.achacha.webcardgame.game.dbo.NexusDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.achacha.test.TestHelper.createNewTestPlayer;
import static org.achacha.webcardgame.game.tick.Nexus.ENERGY_GENERATION_NEXUS_1;
import static org.achacha.webcardgame.game.tick.Nexus.ENERGY_GENERATION_PLAYER;
import static org.achacha.webcardgame.game.tick.Nexus.RESOURCE_PROCESSING_NEXUS_1;
import static org.achacha.webcardgame.game.tick.Nexus.RESOURCE_PROCESSING_PLAYER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NexusTest extends BaseInitializedTest {
    @Test
    void tickPlayerTest() throws SQLException {
        PlayerDbo playerDbo = createNewTestPlayer("nexus_test_0");

        final int startingResources = 150;
        playerDbo.getInventory().setResources(startingResources);

        NexusDbo nexusDbo = new NexusDbo();
        nexusDbo.setPlayerId(playerDbo.getId());
        nexusDbo.setLevel(1);
        Assertions.assertEquals(0, nexusDbo.getResourceProcessingType());
        Assertions.assertEquals(0, nexusDbo.getEnergyGathererType());

        Nexus nexus = new Nexus(playerDbo, nexusDbo);
        assertEquals(nexusDbo, nexus.getDbo());

        Assertions.assertEquals(0, playerDbo.getInventory().getEnergy());
        Assertions.assertEquals(startingResources, playerDbo.getInventory().getResources());
        Assertions.assertEquals(0, playerDbo.getInventory().getMaterials());

        nexus.tick();

        Assertions.assertEquals(ENERGY_GENERATION_PLAYER, playerDbo.getInventory().getEnergy());
        Assertions.assertEquals(startingResources - RESOURCE_PROCESSING_PLAYER, playerDbo.getInventory().getResources());
        Assertions.assertEquals(RESOURCE_PROCESSING_PLAYER, playerDbo.getInventory().getMaterials());
    }

    @Test
    void tickNexus1Test() throws SQLException {
        PlayerDbo playerDbo = createNewTestPlayer("nexus_test_0");

        final int startingResources = 150;
        playerDbo.getInventory().setResources(startingResources);

        NexusDbo nexusDbo = new NexusDbo();
        nexusDbo.setPlayerId(playerDbo.getId());
        nexusDbo.setLevel(1);
        nexusDbo.setEnergyGathererType(1);
        nexusDbo.setResourceProcessingType(1);

        Nexus nexus = new Nexus(playerDbo, nexusDbo);
        assertEquals(nexusDbo, nexus.getDbo());

        Assertions.assertEquals(0, playerDbo.getInventory().getEnergy());
        Assertions.assertEquals(startingResources, playerDbo.getInventory().getResources());
        Assertions.assertEquals(0, playerDbo.getInventory().getMaterials());

        nexus.tick();

        // Generated/processed player base + nexus
        Assertions.assertEquals(ENERGY_GENERATION_PLAYER + ENERGY_GENERATION_NEXUS_1, playerDbo.getInventory().getEnergy());
        Assertions.assertEquals(startingResources - RESOURCE_PROCESSING_NEXUS_1 - RESOURCE_PROCESSING_PLAYER, playerDbo.getInventory().getResources());
        Assertions.assertEquals(RESOURCE_PROCESSING_NEXUS_1 + RESOURCE_PROCESSING_PLAYER, playerDbo.getInventory().getMaterials());
    }
}