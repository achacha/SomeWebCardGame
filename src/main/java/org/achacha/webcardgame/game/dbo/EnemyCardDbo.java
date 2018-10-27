package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.logic.EnemyType;
import org.achacha.webcardgame.game.logic.NameHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Enemy card
 */
@Table(schema="public", name="enemy_card")
public class EnemyCardDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(EnemyCardDbo.class);

    /**
     * Card id
     * If 0 not yet saved
     */
    protected long id;

    /**
     * Encounter that owns this inventory
     * If 0 then not assigned to encounter yet
     */
    protected long encounterId;

    /** Card name */
    protected String name;

    /**
     * Level
     */
    protected int level;

    /**
     * Xp granted when defeated
     */
    protected int xp;

    /**
     * strength - combat damage, damage absorption
     */
    protected int strength;

    /**
     * agility - critical chance, damage avoidance
     */
    protected int agility;

    /**
     * health - total hit-points
     */
    protected int stamina;

    // TODO: Add DB enemy type

    /** Card stickers */
    protected List<EnemyCardStickerDbo> stickers;

    public static Builder builder(EnemyType enemyType, int level) {
        return new Builder(enemyType, level);
    }

    public static class Builder {
        private EnemyCardDbo dbo = new EnemyCardDbo();

        Builder(EnemyType enemyType, int level) {
            dbo.name = NameHelper.generateName(enemyType.getNameType());
            dbo.level = level;
        }

        public EnemyCardDbo build() {
            Preconditions.checkNotNull(dbo.name);
            Preconditions.checkState(dbo.level > 0);
            return dbo;
        }
    }

    public EnemyCardDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.encounterId = rs.getLong("encounter__id");
        this.name = rs.getString("name");

        this.level = rs.getInt("level");
        this.xp = rs.getInt("xp");
        this.strength = rs.getInt("strength");
        this.agility = rs.getInt("agility");
        this.stamina = rs.getInt("stamina");

        this.stickers = Global.getInstance().getDatabaseManager().<EnemyCardStickerDboFactory>getFactory(EnemyCardStickerDbo.class).getByEnemyCardId(id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public long getEncounterId() {
        return encounterId;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getStamina() {
        return stamina;
    }

    public List<EnemyCardStickerDbo> getStickers() {
        return stickers;
    }
}
