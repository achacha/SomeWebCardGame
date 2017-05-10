package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InventoryDboFactory {
    public static InventoryDbo getByLoginId(long loginId) {
        InventoryDbo dbo = null;
        DatabaseManager dm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dm.getConnection();
                PreparedStatement pstmt = dm.prepareStatement(
                        connection,
                        "/sql/Inventory/SelectByLoginId.sql",
                        p -> p.setLong(1, loginId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                dbo = new InventoryDbo();
                dbo.fromResultSet(rs);
            }
        } catch (Exception sqle) {
            ItemDbo.LOGGER.error("Failed to find inventory for loginId={}", loginId, sqle);
        }
        return dbo;

    }
}
