package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDbo;
import org.achacha.webcardgame.game.data.ItemType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Inventory item
 */
@Table(schema="public", name="item")
public class ItemDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(ItemDbo.class);

    /** Item id */
    protected long id;

    /** Inventory that contains this item */
    protected long inventoryId;

    /** Item type */
    protected ItemType type;

    /** Quantity stack */
    protected long quantity;

    public ItemDbo() {
    }

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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public ItemType getType() {
        return type;
    }

    public long getQuantity() {
        return quantity;
    }
}
