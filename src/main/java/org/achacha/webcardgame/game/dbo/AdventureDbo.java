package org.achacha.webcardgame.game.dbo;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.logic.AdventureNameGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Adventure is an instance in progress
 * Contains 1+ encounters and a reward
 */
@Table(schema="public", name="adventure")
public class AdventureDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(AdventureDbo.class);

    /** Adventure id */
    protected long id;

    /** Player that owns this adventure */
    protected long playerId;

    /** Title of this adventure */
    protected String title;

    /** Encounters in this Adventure */
    protected List<EncounterDbo> encounters;

    /** Timestamp for when this adventure was created/activated */
    protected Timestamp created;

    // TODO: Reward (energy and materials)

    /**
     * Create a builder
     * @param playerId player id that will own this adventure
     * @return Builder
     */
    public static Builder builder(long playerId) {
        return new Builder(playerId);
    }

    public static class Builder {
        private AdventureDbo adventure = new AdventureDbo();

        Builder(long playerId) {
            adventure.playerId = playerId;
            adventure.encounters = new ArrayList<>();
        }

        public Builder withEncounter(EncounterDbo encounter) {
            adventure.encounters.add(encounter);
            return this;
        }

        public Builder withTitle(String title) {
            adventure.title = title;
            return this;
        }

        /**
         * @return Build adventure
         */
        public AdventureDbo build() {
            adventure.encounters = new ArrayList<>();

            if (adventure.title == null)
                adventure.title = AdventureNameGenerator.generateAdventureName(adventure);

            return adventure;
        }
    }

    public AdventureDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    @VisibleForTesting
    void setTitle(String title) {
        this.title = title;
    }

    public List<EncounterDbo> getEncounters() {
        return encounters;
    }

    public long getPlayerId() {
        return playerId;
    }

    /**
     * Used to set negative value to differentiate this adventure before the client selects an active
     * @param id id for this adventure
     */
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");
        this.title = rs.getString("title");
        this.encounters = Global.getInstance().getDatabaseManager().<EncounterDboFactory>getFactory(EncounterDbo.class).getByAdventureId(connection, this.id);
        this.created = rs.getTimestamp("created");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    /**
     * Insert new active adventure
     * @param connection Connection to reuse
     * @throws SQLException if failed to insert
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        Preconditions.checkState(playerId > 0);

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(connection,
                        "/sql/Adventure/Insert.sql",
                        p -> {
                            p.setLong(1, playerId);
                            p.setString(2, title);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                this.id = rs.getLong(1);
                this.created = rs.getTimestamp(2);
            }
            else {
                LOGGER.error("Failed to insert active adventure={}", this);
                throw new SQLException("Failed to insert active adventure="+this);
            }
        }

        // id for this adventure is now available
        if (encounters != null) {
            for (EncounterDbo encounter : encounters) {
                encounter.setAdventureId(this.id);
                encounter.insert(connection);
            }
        }
    }
}
