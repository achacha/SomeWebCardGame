package org.achacha.webcardgame.game.tick;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.NexusDbo;
import org.achacha.webcardgame.game.dbo.NexusDboFactory;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class GameSession {
    private static final Logger LOGGER = LogManager.getLogger(GameSession.class);

    private static final long MAXIMUM_TICKS = 10;

    private final Player player;
    private Nexus nexus;
    private Adventure adventure;
    private Timestamp now;

    /** Active Tickable */
    private ArrayList<Tickable> tickables = new ArrayList<>(20);

    /**
     * Load player, nexus and all related data
     * @param playerDbo PlayerDbo
     * @throws SQLException
     */
    public GameSession(Connection connection, PlayerDbo playerDbo) throws SQLException {
        // Get now() from database
        this.now = DatabaseManager.getTimestampNow(connection);

        // Tickable player instance
        this.player = new Player(playerDbo);
        LOGGER.debug("Loaded player={}", player);

        // Load NexusDbo for player
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        NexusDbo nexusDbo = dbm.<NexusDboFactory>getFactory(NexusDbo.class).getByPlayerId(connection, playerDbo.getId());
        this.nexus = new Nexus(playerDbo, nexusDbo);
        LOGGER.debug("Loaded nexus={}", nexus);

        // One adventure, multiple encounters
        AdventureDbo currentAdventure = dbm.<AdventureDboFactory>getFactory(AdventureDbo.class).getByPlayerId(connection, playerDbo.getId());
        if (currentAdventure != null) {
            this.adventure = new Adventure(playerDbo, currentAdventure);
            LOGGER.debug("Loaded adventure={}", adventure);
        }
        else {
            LOGGER.debug("No active adventure");

        }

        // Determine all tickables
        tickables.add(player);
        tickables.add(nexus);
        tickables.add(adventure);
        // TODO: more here like buffs and other temporary effects
    }

    public void process() {
        // Determine how many ticks have gone by up to maximum
        long ticks = Long.min(MAXIMUM_TICKS, TickHelper.ticksBetweenTimestamps(player.getPlayer().getLastTick(), now));
        while (ticks-- > 0) {
            tickables.forEach(Tickable::tick);

            // Add all completed?
            tickables.stream().filter(Tickable::isComplete).forEach(Tickable::onComplete);

            // Remove completed from active
            tickables.removeIf(Tickable::isComplete);
        }

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            player.getPlayer().updateLastTickToNow(connection);
        }
        catch(SQLException e) {
            LOGGER.error("Failed to update lastTick on player="+player.getPlayer(), e);
        }
    }
}
