package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Encounter
 */
public class EncounterDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(EncounterDbo.class);

    /** Item id */
    protected long id;

    /** Associated adventure */
    protected long adventureId;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.adventureId = rs.getLong("adventure__id");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public long getAdventureId() {
        return adventureId;
    }
}
