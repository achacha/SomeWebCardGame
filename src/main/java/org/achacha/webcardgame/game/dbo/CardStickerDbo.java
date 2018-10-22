package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Card Sticker
 * Contains special things that the card can do
 */
@Table(schema="public", name="card_sticker")
public class CardStickerDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(CardStickerDbo.class);

    /** Card id */
    protected long id;

    /** Card that this sticker is attached to */
    protected long cardId;

    /** Sticker name */
    protected String name;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.name = rs.getString("name");
        this.cardId = rs.getLong("card__id");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }
}
