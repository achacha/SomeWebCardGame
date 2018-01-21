package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdventureDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(AdventureDbo.class);

    public static List<AdventureDbo> getByPlayerId(long playerId) {
        List<AdventureDbo> dbos = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Adventure/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                AdventureDbo dbo = new AdventureDbo();
                dbo.fromResultSet(rs);
                dbos.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find adventures for playerId={}", playerId, sqle);
        }
        return dbos;

    }
}
