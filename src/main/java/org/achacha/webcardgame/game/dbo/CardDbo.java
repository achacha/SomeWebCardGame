package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Card
 */
public class CardDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(CardDbo.class);

    /** Card id */
    protected long id;

    /** Player that owns this inventory */
    protected long playerId;

    /** Card name */
    protected String name;

    /**
     * Level
     */
    protected int level;

    /**
     * Experience into the level
     * when xp > 100,000 then level is increased
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

    /** Card stickers */
    protected Collection<CardStickerDbo> stickers;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.playerId = rs.getLong("player__id");
        this.name = rs.getString("name");

        this.level = rs.getInt("level");
        this.xp = rs.getInt("xp");
        this.strength = rs.getInt("strength");
        this.agility = rs.getInt("agility");
        this.stamina = rs.getInt("stamina");

        this.stickers = CardStickerDboFactory.getByCardId(id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public long getPlayerId() {
        return playerId;
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

    public Collection<CardStickerDbo> getStickers() {
        return stickers;
    }
}
