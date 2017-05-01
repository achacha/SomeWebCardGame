package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.webcardgame.game.data.ItemType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inventory item
 */
public class ItemDbo extends BaseIndexedDbo {
    static final Logger LOGGER = LogManager.getLogger(ItemDbo.class);

    protected long id;
    protected long inventoryId;
    protected ItemType type;
    protected long quantity;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.inventoryId = rs.getLong("inventory__id");
        this.type = ItemType.of(rs.getInt("type"));
        this.quantity = rs.getLong("quantity");
    }
}
