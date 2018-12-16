package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Player
 */
@Table(schema="public", name="player")
public class PlayerDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(PlayerDbo.class);

    /** Player id */
    protected long id;

    /** Login that owns this player */
    protected long loginId;

    /** Inventory */
    protected InventoryDbo inventory;

    /** Cards */
    protected List<CardDbo> cards;

    public PlayerDbo() {
    }

    public static Builder builder(long loginId) {
        return new Builder(loginId);
    }

    public static class Builder {
        final long loginId;

        public Builder(long loginId) {
            this.loginId = loginId;
        }

        public PlayerDbo build() {
            PlayerDbo player = new PlayerDbo();
            player.loginId = loginId;
            player.inventory = new InventoryDbo();
            player.cards = new ArrayList<>();
            return player;
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.loginId = rs.getLong("login__id");

        this.inventory = Global.getInstance().getDatabaseManager().<InventoryDboFactory>getFactory(InventoryDbo.class).getByPlayerId(this.id);
        this.cards = Global.getInstance().getDatabaseManager().<CardDboFactory>getFactory(CardDbo.class).getByPlayerId(this.id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        Preconditions.checkState(loginId > 0);

        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Player/Insert.sql",
                        p-> {
                            p.setLong(1, loginId);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                this.id = rs.getLong(1);
            }
            else {
                LOGGER.error("Failed to insert player={}", this);
                throw new SQLException("Failed to insert player="+this);
            }

            // Insert inventory
            inventory.playerId = this.id;
            inventory.insert(connection);

            // Insert cards if any
            for (CardDbo card : cards) {
                card.playerId = this.id;
                card.insert(connection);
            }
        }
    }

    @Override
    public void update(Connection connection) throws SQLException {
        Preconditions.checkState(id > 0);
        Preconditions.checkState(loginId > 0);

        // Player doesn't have any updatable data at this time, propagate changes to members

        inventory.update(connection);

        //TODO: How to handle deleted cards (do we support that?) should we just flag it inactive?
        for (CardDbo card : cards) {
            if (card.id == 0) {
                card.playerId = this.id;
                card.insert(connection);
            }
            else {
                card.update(connection);
            }
        }
    }

    public List<CardDbo> getCards() {
        return cards;
    }

    public void setCards(List<CardDbo> cards) {
        this.cards = cards;
    }

    public InventoryDbo getInventory() {
        return inventory;
    }

    public void setInventory(InventoryDbo inventory) {
        this.inventory = inventory;
    }
}
