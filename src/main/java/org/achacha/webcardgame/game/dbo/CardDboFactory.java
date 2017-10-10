package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class CardDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(InventoryDbo.class);

    public static Collection<CardDbo> getByPlayerId(long playerId) {
        ArrayList<CardDbo> dbos = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Card/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                CardDbo dbo = new CardDbo();
                dbo.fromResultSet(rs);
                dbos.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find cards for playerId={}", playerId, sqle);
        }
        return dbos;

    }
}
