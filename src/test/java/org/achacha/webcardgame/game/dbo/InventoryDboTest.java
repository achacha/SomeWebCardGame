package org.achacha.webcardgame.game.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventoryDboTest extends BaseInitializedTest {
    private InventoryDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(InventoryDbo.class);

    @Test
    void testGetInventoryForLogin() {
        InventoryDbo inventory = factory.getByPlayerId(TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(inventory);
        assertEquals(6, inventory.getItems().size());
    }
}
