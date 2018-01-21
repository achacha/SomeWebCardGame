package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EnemyCardDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(EnemyCardDbo.class);

    public static List<EnemyCardDbo> getByEncounterId(long encounterId) {
        ArrayList<EnemyCardDbo> dbos = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/EnemyCard/SelectByEncounterId.sql",
                        p -> p.setLong(1, encounterId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                EnemyCardDbo dbo = new EnemyCardDbo();
                dbo.fromResultSet(rs);
                dbos.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find enemy cards for encounter={}", encounterId, sqle);
        }
        return dbos;

    }
}
