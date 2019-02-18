package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    protected List<ItemDbo> items = new ArrayList<>();

    /** Energy */
    long energy;

    /** Materials */
    long materials;

    /** Raw resources */
    long resources;

    public static Builder builder(PlayerDbo player) {
        return new Builder(player);
    }

    public static class Builder {
        private InventoryDbo inventory = new InventoryDbo();

        public Builder(PlayerDbo player) {
            inventory.playerId = player.id;
        }

        public Builder withEnergy(long energy) {
            inventory.energy = energy;
            return this;
        }

        public Builder withMaterials(long materials) {
            inventory.materials = materials;
            return this;
        }

        public Builder withResources(long resources) {
            inventory.resources = resources;
            return this;
        }

        public InventoryDbo build() {
            return inventory;
        }
    }

    InventoryDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");

        this.energy = rs.getLong("energy");
        this.resources = rs.getLong("resources");
        this.materials = rs.getLong("materials");

        this.items = Global.getInstance().getDatabaseManager().<ItemDboFactory>getFactory(ItemDbo.class).getByInventoryId(connection, this.id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        Preconditions.checkState(playerId > 0);

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Inventory/Insert.sql",
                        p-> {
                            p.setLong(1, playerId);
                            p.setLong(2, energy);
                            p.setLong(3, resources);
                            p.setLong(4, materials);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                this.id = rs.getLong(1);
            }
            else {
                LOGGER.error("Failed to insert inventory={}", this);
                throw new SQLException("Failed to insert inventory="+this);
            }

            for (ItemDbo item : items) {
                item.inventoryId = this.id;
                item.insert(connection);
            }
        }
    }

    @Override
    public void update(Connection connection) throws SQLException {
        Preconditions.checkState(id > 0);
        Preconditions.checkState(playerId > 0);

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Inventory/Update.sql",
                        p-> {
                            p.setLong(1, energy);
                            p.setLong(2, resources);
                            p.setLong(3, materials);

                            p.setLong(4, id);
                        }
                )
        ) {
            pstmt.executeUpdate();

            // Remove all and update contained items
            Global.getInstance().getDatabaseManager().<ItemDboFactory>getFactory(ItemDbo.class).deleteAllByInventoryId(connection, id);
            for (ItemDbo item : items) {
                if (item.id == 0) {
                    item.inventoryId = this.id;
                    item.insert(connection);
                }
                else {
                    item.update(connection);
                }
            }
        }
    }

    public void clear() {
        this.energy = 0;
        this.materials = 0;
        this.items.clear();
    }

    public List<ItemDbo> getItems() {
        return items;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getEnergy() {
        return energy;
    }

    public void addEnergy(long energyToAdd) {
        // TODO: Handle max energy
        this.energy += energyToAdd;
    }

    public void setEnergy(long energy) {
        // TODO: Handle max energy
        this.energy = energy;
    }

    public long getMaterials() {
        return materials;
    }

    public void setMaterials(long materials) {
        this.materials = materials;
    }

    public long getResources() {
        return resources;
    }

    public void setResources(long resources) {
        this.resources = resources;
    }
}
