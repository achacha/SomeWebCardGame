package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemDboFactory extends BaseDboFactory<ItemDbo> {
    public ItemDboFactory() {
        super(ItemDbo.class);
    }

    /**
     * Get a list of items for a given inventory
     * @param connection Connection
     * @param inventoryId long
     * @return List of items (never null)
     *
     * @Deprecated Don't know if we need this since InventoryDbo will fetch this
     */
    @Nonnull
    public List<ItemDbo> getByInventoryId(Connection connection, long inventoryId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        List<ItemDbo> items = new ArrayList<>();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Item/SelectByInventoryId.sql",
                        p -> p.setLong(1, inventoryId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                ItemDbo dbo = new ItemDbo();
                dbo.fromResultSet(connection, rs);
                items.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find item for inventoryId={}", inventoryId, sqle);
        }
        return items;
    }

    /**
     * Delete all items for a given inventory
     * @param connection Connection
     * @param inventoryId long
     */
    public void deleteAllByInventoryId(Connection connection, long inventoryId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Item/DeleteAllByInventoryId.sql",
                        p -> p.setLong(1, inventoryId))
        ) {
            pstmt.executeUpdate();
        } catch (Exception sqle) {
            LOGGER.error("Failed to delete all for inventoryId={}", inventoryId, sqle);
        }
    }
}
