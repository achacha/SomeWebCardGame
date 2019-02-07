package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NexusDboFactory extends BaseDboFactory<NexusDbo> {
    public NexusDboFactory() {
        super(NexusDbo.class);
    }

    /**
     * Load nexus for player
     * @param connection Connection
     * @param playerId PlayerDbo id
     * @return NexusDbo or null if none
     */
    @Nullable
    public NexusDbo getByPlayerId(Connection connection, long playerId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        NexusDbo dbo = null;
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Nexus/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                dbo = new NexusDbo();
                dbo.fromResultSet(connection, rs);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find nexus for playerId={}", playerId, sqle);
        }
        return dbo;

    }
}
