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

/**
 * Nexus
 */
@Table(schema="public", name="nexus")
public class NexusDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(NexusDbo.class);

    /** Inventory id */
    protected long id;

    /** Player that owns this inventory */
    protected long playerId;

    /**
     * Nexus level
     */
    int level;

    /**
     * TODO: enum
     */
    int energyGathererType;

    /**
     * TODO: enum
     */
    int materialProcessingType;

//    public static Builder builder(PlayerDbo player) {
//        return new Builder(player);
//    }
//
//    public static class Builder {
//        private NexusDbo inventory = new NexusDbo();
//
//        public Builder(PlayerDbo player) {
//            inventory.playerId = player.id;
//        }
//
//        public Builder withEnergy(long energy) {
//            inventory.energy = energy;
//            return this;
//        }
//
//        public Builder withMaterials(long materials) {
//            inventory.materials = materials;
//            return this;
//        }
//
//        public NexusDbo build() {
//            return inventory;
//        }
//    }

    NexusDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");

        this.level = rs.getInt("level");
        this.energyGathererType = rs.getInt("energy_gatherer_type");
        this.materialProcessingType = rs.getInt("material_processing_type");

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
                        "/sql/Nexus/Insert.sql",
                        p-> {
                            p.setLong(1, playerId);
                            p.setLong(2, level);
                            p.setLong(3, energyGathererType);
                            p.setLong(4, materialProcessingType);
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
        }
    }

    @Override
    public void update(Connection connection) throws SQLException {
        Preconditions.checkState(id > 0);
        Preconditions.checkState(playerId > 0);

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Nexus/Update.sql",
                        p-> {
                            p.setLong(1, level);
                            p.setLong(2, energyGathererType);
                            p.setLong(3, materialProcessingType);

                            p.setLong(4, id);
                        }
                )
        ) {
            pstmt.executeUpdate();
        }
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getLevel() {
        return level;
    }

    public int getEnergyGathererType() {
        return energyGathererType;
    }

    public int getMaterialProcessingType() {
        return materialProcessingType;
    }
}
