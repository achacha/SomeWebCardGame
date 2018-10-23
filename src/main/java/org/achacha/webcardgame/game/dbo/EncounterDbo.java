package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Encounter
 */
@Table(schema="public", name="encounter")
public class EncounterDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(EncounterDbo.class);

    /** Item id */
    protected long id;

    /** Associated adventure */
    protected long adventureId;

    // We may want more than 1 enemy?
    protected List<EnemyCardDbo> enemies;

    public EncounterDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.adventureId = rs.getLong("adventure__id");

        enemies = Global.getInstance().getDatabaseManager().<EnemyCardDboFactory>getFactory(EnemyCardDbo.class).getByEncounterId(id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public long getAdventureId() {
        return adventureId;
    }

    public List<EnemyCardDbo> getEnemies() {
        return enemies;
    }
}
