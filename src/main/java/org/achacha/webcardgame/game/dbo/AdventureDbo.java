package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Adventure
 */
public class AdventureDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(AdventureDbo.class);

    /** Adventure id */
    protected long id;

    /** Player that owns this adventure */
    protected long playerId;

    /** Encounters in this Adventure */
    protected Collection<EncounterDbo> encounters;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");
        this.encounters = EncounterDboFactory.getEncountersForAdventure(this.id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public Collection<EncounterDbo> getEncounters() {
        return encounters;
    }
}
