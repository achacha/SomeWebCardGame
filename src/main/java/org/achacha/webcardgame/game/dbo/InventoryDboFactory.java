package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(InventoryDbo.class);

    public static InventoryDbo getByPlayerId(long playerId) {
        InventoryDbo dbo = null;
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Inventory/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                dbo = new InventoryDbo();
                dbo.fromResultSet(rs);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find inventory for playerId={}", playerId, sqle);
        }
        return dbo;

    }
}
