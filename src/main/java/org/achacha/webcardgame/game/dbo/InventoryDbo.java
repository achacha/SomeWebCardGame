package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Inventory
 */
public class InventoryDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(InventoryDbo.class);

    protected long id;
    protected long playerId;
    protected long energy;
    protected List<ItemDbo> items;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");
        this.energy = rs.getLong("energy");
        this.items = ItemDboFactory.getItemsForInventory(this.id);
    }

    public long getEnergy() {
        return energy;
    }

    public List<ItemDbo> getItems() {
        return items;
    }
}
