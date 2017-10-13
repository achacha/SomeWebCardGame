package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseIndexedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Player
 */
public class PlayerDbo extends BaseIndexedDbo {
    private static final Logger LOGGER = LogManager.getLogger(PlayerDbo.class);

    /** Player id */
    protected long id;

    /** Login that owns this player */
    protected long loginId;

    /** Energy stores in the inventory */
    protected long energy;

    /** Inventory */
    protected InventoryDbo inventory;

    /** Cards */
    protected Collection<CardDbo> cards;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.loginId = rs.getLong("login__id");
        this.energy = rs.getLong("energy");

        this.inventory = InventoryDboFactory.getByPlayerId(this.id);
        this.cards = CardDboFactory.getByPlayerId(this.id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public long getEnergy() {
        return energy;
    }
}
