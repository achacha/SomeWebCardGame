package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class CardStickerDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(CardStickerDbo.class);

    public static Collection<CardStickerDbo> getByCardId(long cardId) {
        ArrayList<CardStickerDbo> dbos = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/CardSticker/SelectByCardId.sql",
                        p -> p.setLong(1, cardId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                CardStickerDbo dbo = new CardStickerDbo();
                dbo.fromResultSet(rs);
                dbos.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find card stickers for cardId={}", cardId, sqle);
        }
        return dbos;

    }
}
