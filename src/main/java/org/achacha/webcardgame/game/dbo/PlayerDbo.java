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
import java.sql.Timestamp;
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

    /** Player name */
    protected String name;

    /** Timestamp of the last processed tick */
    protected Timestamp lastTick;

    /** Inventory */
    protected InventoryDbo inventory;

    /** Cards */
    protected List<CardDbo> cards;

    public static Builder builder(long loginId, String name) {
        return new Builder(loginId, name);
    }

    public static class Builder {
        private final PlayerDbo player = new PlayerDbo();

        public Builder(long loginId, String name) {
            player.loginId = loginId;
            player.name = name;
            player.cards = new ArrayList<>();
            player.inventory = new InventoryDbo();
        }

        /**
         * Add a card
         * NOTE: You don't need player id on this card, it will be correctly adjusted on insert
         * @param card CardDbo
         * @return Builder
         */
        public Builder withCard(CardDbo card) {
            player.cards.add(card);
            return this;
        }

        public PlayerDbo build() {
            return player;
        }
    }

    public PlayerDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    public Timestamp getLastTick() {
        return lastTick;
    }

    public String getName() {
        return name;
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.loginId = rs.getLong("login__id");
        this.name = rs.getString("name");
        this.lastTick = rs.getTimestamp("last_tick");

        this.inventory = Global.getInstance().getDatabaseManager().<InventoryDboFactory>getFactory(InventoryDbo.class).getByPlayerId(connection, this.id);
        this.cards = Global.getInstance().getDatabaseManager().<CardDboFactory>getFactory(CardDbo.class).getByPlayerId(connection, this.id);

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
                            p.setString(2, name);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                this.id = rs.getLong(1);
                this.lastTick = rs.getTimestamp(2);
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

        // Inactivate cards not part of the existing list
        Global.getInstance().getDatabaseManager().<CardDboFactory>getFactory(CardDbo.class).inactivateNotIn(connection, id, cards);
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

    /**
     * Set the last tick update to now()
     * Commits on update
     * @param connection Connection
     */
    public void updateLastTickToNow(Connection connection) throws SQLException {
        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Player/UpdateLastTickToNow.sql",
                        p -> {
                            p.setLong(1, id);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            connection.commit();
            Preconditions.checkState(rs.next());
            lastTick = rs.getTimestamp(1);
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
