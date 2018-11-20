package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.data.CardType;
import org.apache.commons.lang3.RandomUtils;
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
 * Adventure is an instance in progress
 * Contains 1+ encounters and a reward
 */
@Table(schema="public", name="adventure")
public class AdventureDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(AdventureDbo.class);

    /** Adventure id */
    protected long id;

    /** Player that owns this adventure */
    protected long playerId;

    /** Encounters in this Adventure */
    protected List<EncounterDbo> encounters;

    /** If active adventure */
    boolean active;

    // TODO: Reward

    public static Builder builder(int encounters, int level) {
        return new Builder(encounters, level);
    }

    public static class Builder {
        private final int encounters;
        private final int level;

        Builder(int encounters, int level) {
            this.encounters = encounters;
            this.level = level;
        }

        /**
         * @return Build random adventure
         */
        public AdventureDbo build() {
            Preconditions.checkState(encounters > 0);

            AdventureDbo adventure = new AdventureDbo();
            adventure.encounters = new ArrayList<>(encounters);
            for (int i = 0; i < encounters; ++i) {
                int enemies = RandomUtils.nextInt(1,3);
                CardType enemyType = CardType.random();
                adventure.encounters.add(EncounterDbo.builder(enemyType, enemies, level).build());
            }
            return adventure;
        }
    }

    AdventureDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Used to set negative value to differentiate this adventure before the client selects an active
     * @param id id for this adventure
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Set this adventure active
     * @param active boolean
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");
        this.encounters = Global.getInstance().getDatabaseManager().<EncounterDboFactory>getFactory(EncounterDbo.class).getEncountersForAdventure(this.id);
        this.active = rs.getBoolean("active");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    @Override
    public void insert(Connection connection) throws Exception {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();

        for (EncounterDbo encounter : encounters) {
            encounter.insert(connection);
        }

        try (
                PreparedStatement pstmt = dbm.prepareStatement(connection,
                        "/sql/Adventure/Insert.sql",
                        p -> {
                            p.setLong(1, playerId);
                            p.setBoolean(2, active);
                        }
                )
        ) {
            pstmt.executeUpdate();
        }

    }

    public List<EncounterDbo> getEncounters() {
        return encounters;
    }
}
