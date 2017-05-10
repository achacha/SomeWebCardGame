package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Inventory
 */
public class InventoryDbo extends BaseIndexedDbo {
    protected long id;
    protected long loginId;
    protected long energy;
    protected List<ItemDbo> items;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.loginId = rs.getLong("login__id");
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
