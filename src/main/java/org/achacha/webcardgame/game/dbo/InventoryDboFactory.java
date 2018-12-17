package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryDboFactory extends BaseDboFactory<InventoryDbo> {
    public InventoryDboFactory() {
        super(InventoryDbo.class);
    }

    public InventoryDbo getByPlayerId(Connection connection, long playerId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        InventoryDbo dbo = null;
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Inventory/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                dbo = new InventoryDbo();
                dbo.fromResultSet(connection, rs);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find inventory for playerId={}", playerId, sqle);
        }
        return dbo;

    }
}
