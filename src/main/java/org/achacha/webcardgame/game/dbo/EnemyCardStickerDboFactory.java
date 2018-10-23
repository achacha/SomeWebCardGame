package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EnemyCardStickerDboFactory extends BaseDboFactory<EnemyCardStickerDbo> {
    public EnemyCardStickerDboFactory(Class<EnemyCardStickerDbo> clz) {
        super(clz);
    }

    public List<EnemyCardStickerDbo> getByEnemyCardId(long cardId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        ArrayList<EnemyCardStickerDbo> dbos = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/EnemyCardSticker/SelectByEnemyCardId.sql",
                        p -> p.setLong(1, cardId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                EnemyCardStickerDbo dbo = new EnemyCardStickerDbo();
                dbo.fromResultSet(rs);
                dbos.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find enemy card stickers for enemyCardId={}", cardId, sqle);
        }
        return dbos;

    }
}
