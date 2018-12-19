package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardDboFactory extends BaseDboFactory<CardDbo> {
    public CardDboFactory() {
        super(CardDbo.class);
    }

    public List<CardDbo> getByPlayerId(Connection connection, long playerId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        ArrayList<CardDbo> dbos = new ArrayList<>();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Card/SelectByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                CardDbo dbo = new CardDbo();
                dbo.fromResultSet(connection, rs);
                dbos.add(dbo);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to find cards for playerId={}", playerId, e);
        }
        return dbos;
    }

    // TODO: Unit test or remove
    public List<CardDbo> getByEncounterId(Connection connection, long encounterId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        ArrayList<CardDbo> dbos = new ArrayList<>();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Card/SelectByEncounterId.sql",
                        p -> p.setLong(1, encounterId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                CardDbo dbo = new CardDbo();
                dbo.fromResultSet(connection, rs);
                dbos.add(dbo);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to find cards for encounterId={}", encounterId, e);
        }
        return dbos;

    }

    public void deleteNotIn(Connection connection, long playerId, List<CardDbo> existingCards) throws SQLException {
        Preconditions.checkState(playerId > 0);

        if (existingCards.size() > 0) {
            DatabaseManager dbm = Global.getInstance().getDatabaseManager();
            try (PreparedStatement pstmt = dbm.prepareStatement(
                    connection,
                    "/sql/Card/DeleteNotIn.sql",
                    p -> {
                        p.setLong(1, playerId);

                        // Get all ids >0 and delete anything that is no longer present, id == 0 means it was not yet inserted
                        Object[] ids = existingCards.stream().map(CardDbo::getId).filter(id-> id > 0).toArray();
                        Array ary = connection.createArrayOf("INTEGER", ids);
                        p.setArray(2, ary);
                    }
            )) {
                pstmt.executeUpdate();
            }
        }
    }
}
