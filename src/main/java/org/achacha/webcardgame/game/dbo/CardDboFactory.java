package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

}
