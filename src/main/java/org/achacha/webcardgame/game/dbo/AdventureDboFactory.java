package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdventureDboFactory extends BaseDboFactory<AdventureDbo> {
    public AdventureDboFactory() {
        super(AdventureDbo.class);
    }

    /**
     * Get active adventure for the player id
     * @param connection Connection
     * @param playerId long
     * @return AdventureDbo or null
     */
    @Nullable
    public AdventureDbo getByPlayerId(Connection connection, long playerId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Adventure/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                return createFromResultSet(connection, rs);
            }
            if (rs.next()) {
                LOGGER.error("Unexpected, playerId={} is on more than 1 adventure", playerId);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find adventures for playerId={}", playerId, sqle);
        }
        return null;
    }

    /**
     * Delete active for a player
     * @param connection Connection to reuse
     * @param playerId long
     */
    public void deleteAllByPlayerId(Connection connection, long playerId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Adventure/DeleteAllByPlayerId.sql",
                        p-> p.setLong(1, playerId));
        ) {
            pstmt.executeUpdate();
        } catch (Exception sqle) {
            LOGGER.error("Failed to delete all adventures for playerId={}", playerId, sqle);
        }
    }
}
