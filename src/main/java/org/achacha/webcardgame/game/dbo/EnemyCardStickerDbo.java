package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Enemy card Sticker
 * Contains special things that the enemy card can do
 */
@Table(schema="public", name="enemy_card_sticker")
public class EnemyCardStickerDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(EnemyCardStickerDbo.class);

    /** Card id */
    protected long id;

    /** Enemy card that this sticker is attached to */
    protected long enemyCardId;

    /** Sticker name */
    protected String name;

    public EnemyCardStickerDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.name = rs.getString("name");
        this.enemyCardId = rs.getLong("enemy_card__id");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }
}
