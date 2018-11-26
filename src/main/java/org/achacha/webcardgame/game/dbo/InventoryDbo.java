package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDbo;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Inventory
 */
@Table(schema="public", name="inventory")
public class InventoryDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(InventoryDbo.class);

    /** Inventory id */
    protected long id;

    /** Player that owns this inventory */
    protected long playerId;

    /** Items in this inventory */
    protected List<ItemDbo> items;

    public InventoryDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");

        this.items = Global.getInstance().getDatabaseManager().<ItemDboFactory>getFactory(ItemDbo.class).getItemsForInventory(this.id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public List<ItemDbo> getItems() {
        return items;
    }

    public void setItems(List<ItemDbo> items) {
        this.items = items;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
