package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.logic.CardType;
import org.achacha.webcardgame.game.logic.NameHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encounter associated with adventure
 */
@Table(schema="public", name="encounter")
public class EncounterDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(EncounterDbo.class);

    /** Item id */
    protected long id;

    /** Associated adventure */
    protected long adventureId;

    /** Enemies in this encounter */
    protected List<CardDbo> enemies;

    public static Builder builder(CardType enemyType, int enemies, int level) {
        return new Builder(enemyType, enemies, level);
    }

    public static class Builder {
        private final int enemies;
        private final int level;
        private final CardType enemyType;

        Builder(CardType enemyType, int enemies, int level) {
            this.enemyType = enemyType;
            this.enemies = enemies;
            this.level = level;
        }

        public EncounterDbo build() {
            Preconditions.checkState(enemies > 0);

            EncounterDbo encounter = new EncounterDbo();
            encounter.enemies = new ArrayList<>(enemies);
            for (int i = 0; i < enemies; ++i) {
                CardDbo enemyCard = new CardDbo();
                enemyCard.setLevel(level);
                enemyCard.setName(NameHelper.generateName(enemyType.getNameType()));
                encounter.enemies.add(enemyCard);
            }
            return encounter;
        }
    }

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

        enemies = Global.getInstance().getDatabaseManager().<CardDboFactory>getFactory(CardDbo.class).getByEncounterId(id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public long getAdventureId() {
        return adventureId;
    }

    public List<CardDbo> getEnemies() {
        return enemies;
    }
}
