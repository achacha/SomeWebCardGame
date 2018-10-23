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
     * @param inventoryId long
     * @return List of items (never null)
     */
    @Nonnull
    public List<ItemDbo> getItemsForInventory(long inventoryId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        List<ItemDbo> items = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Item/SelectByInventoryId.sql",
                        p -> p.setLong(1, inventoryId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                ItemDbo dbo = new ItemDbo();
                dbo.fromResultSet(rs);
                items.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find item for inventoryId={}", inventoryId, sqle);
        }
        return items;
    }
}
