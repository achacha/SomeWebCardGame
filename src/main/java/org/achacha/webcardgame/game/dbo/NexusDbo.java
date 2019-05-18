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
    int resourceProcessingType;

    /**
     * Total raw resources available for processing
     */
    int rawResourcesAvailable;

    public NexusDbo() {
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
        this.rawResourcesAvailable = rs.getInt("raw_resources_available");
        this.energyGathererType = rs.getInt("energy_gatherer_type");
        this.resourceProcessingType = rs.getInt("material_processing_type");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        Preconditions.checkState(playerId > 0, "There must be a valid player associated");

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Nexus/Insert.sql",
                        p-> {
                            p.setLong(1, playerId);
                            p.setLong(2, level);
                            p.setLong(3, rawResourcesAvailable);
                            p.setLong(4, energyGathererType);
                            p.setLong(5, resourceProcessingType);
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
        Preconditions.checkState(id > 0, "Update can only be performed on an existing object");
        Preconditions.checkState(playerId > 0, "There must be a valid player associated");

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Nexus/Update.sql",
                        p-> {
                            p.setLong(1, level);
                            p.setLong(2, rawResourcesAvailable);
                            p.setLong(3, energyGathererType);
                            p.setLong(4, resourceProcessingType);

                            p.setLong(5, id);
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

    public void setLevel(int level) {
        this.level = level;
    }

    public void setEnergyGathererType(int energyGathererType) {
        this.energyGathererType = energyGathererType;
    }

    public void setResourceProcessingType(int resourceProcessingType) {
        this.resourceProcessingType = resourceProcessingType;
    }

    public int getEnergyGathererType() {
        return energyGathererType;
    }

    public int getResourceProcessingType() {
        return resourceProcessingType;
    }
}
