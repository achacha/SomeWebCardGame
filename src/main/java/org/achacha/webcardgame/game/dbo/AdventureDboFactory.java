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
     * @param playerId long
     * @return AdventureDbo or null
     */
    @Nullable
    public AdventureDbo getByPlayerId(long playerId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Adventure/SelectActiveByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                AdventureDbo dbo = new AdventureDbo();
                dbo.fromResultSet(rs);
                return dbo;
            }
            if (rs.next()) {
                LOGGER.error("Unexpected, playerId={} is on more than 1 adventure", playerId);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find adventures for playerId={}", playerId, sqle);
        }
        return null;
    }
}
