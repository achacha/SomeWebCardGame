package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.data.ItemType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    /**
     * Builder for an item of type
     * @param type ItemType
     * @return Builder
     */
    public static Builder builder(InventoryDbo inventory, ItemType type) {
        return new Builder(inventory.getId(), type);
    }

    public static class Builder {
        final long inventoryId;
        final ItemType type;
        long quantity = 1;

        Builder(long inventoryId, ItemType type) {
            this.type = type;
            this.inventoryId = inventoryId;
        }

        public Builder withQuantity(long quantity) {
            this.quantity = quantity;
            return this;
        }

        ItemDbo build() {
            ItemDbo item = new ItemDbo();
            item.type = type;
            item.quantity = quantity;
            return item;
        }
    }

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

    @Override
    public void insert(Connection connection) throws SQLException {
        Preconditions.checkState(inventoryId > 0);

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Item/Insert.sql",
                        p-> {
                            p.setLong(1, inventoryId);
                            p.setInt(2, type.ordinal());
                            p.setLong(3, quantity);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                this.id = rs.getLong(1);
            }
            else {
                LOGGER.error("Failed to insert item={}", this);
                throw new SQLException("Failed to insert item="+this);
            }
        }
    }

    @Override
    public void update(Connection connection) throws SQLException {
        Preconditions.checkState(id > 0);
        Preconditions.checkState(inventoryId > 0);

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Item/Update.sql",
                        p-> {
                            p.setInt(1, type.ordinal());
                            p.setLong(2, quantity);

                            p.setLong(3, id);
                        }
                )
        ) {
            pstmt.executeUpdate();
        }
    }

    public ItemType getType() {
        return type;
    }

    public long getQuantity() {
        return quantity;
    }
}
