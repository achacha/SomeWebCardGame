package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Player
 */
public class PlayerDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(PlayerDbo.class);

    /** Player id */
    protected long id;

    /** Login that owns this player */
    protected long loginId;

    /** Inventory */
    protected InventoryDbo inventory;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.loginId = rs.getLong("login__id");
        this.inventory = InventoryDboFactory.getByPlayerId(this.id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }
}
