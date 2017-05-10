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

    protected long id;
    protected long loginId;
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
    }

}
