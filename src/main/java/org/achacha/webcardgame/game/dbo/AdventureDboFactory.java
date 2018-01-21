package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdventureDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(InventoryDbo.class);

    public static AdventureDbo getByPlayerId(long playerId) {
        AdventureDbo dbo = null;
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Adventure/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                dbo = new AdventureDbo();
                dbo.fromResultSet(rs);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find adventure for playerId={}", playerId, sqle);
        }
        return dbo;

    }
}
