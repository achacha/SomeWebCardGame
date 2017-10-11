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
        this.stickers = CardStickerDboFactory.getByCardId(id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public Collection<CardStickerDbo> getStickers() {
        return stickers;
    }
}
