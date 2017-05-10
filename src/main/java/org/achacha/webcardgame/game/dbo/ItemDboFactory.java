package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(ItemDbo.class);

    /**
     * Get a list of items for a given inventory
     * @param inventoryId long
     * @return List of items (never null)
     */
    @Nonnull
    public static List<ItemDbo> getItemsForInventory(long inventoryId) {
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
