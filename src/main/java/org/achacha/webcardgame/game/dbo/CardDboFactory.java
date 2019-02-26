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

    /**
     * Get all active cards for the player
     * @param connection Connection
     * @param playerId long
     * @return List of active CardDbo
     */
    public List<CardDbo> getByPlayerId(Connection connection, long playerId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        ArrayList<CardDbo> dbos = new ArrayList<>();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Card/SelectActiveByPlayerId.sql",
                        p -> p.setLong(1, playerId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                CardDbo dbo = new CardDbo();
                dbo.fromResultSet(connection, rs);
                dbos.add(dbo);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to find active cards for playerId={}", playerId, e);
        }
        return dbos;
    }

    /**
     * Get cards for specific ids
     * @param connection Connection
     * @param ids Array of ids
     * @return List of CardDbo
     * @throws SQLException on DB error
     */
    //TODO: Test
    public List<CardDbo> getByIds(Connection connection, Array ids) throws SQLException {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        ArrayList<CardDbo> dbos = new ArrayList<>();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Card/SelectByIds.sql",
                        p -> p.setArray(1, ids));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                CardDbo dbo = new CardDbo();
                dbo.fromResultSet(connection, rs);
                dbos.add(dbo);
            }
        }
        return dbos;

    }

    /**
     * Mark existing cards inactive
     * @param connection Connection
     * @param playerId long
     * @param existingCards List of existing cards to make inactive
     * @throws SQLException on DB error
     */
    // TODO: Test
    public void inactivateNotIn(Connection connection, long playerId, List<CardDbo> existingCards) throws SQLException {
        Preconditions.checkState(playerId > 0);

        if (existingCards.size() > 0) {
            DatabaseManager dbm = Global.getInstance().getDatabaseManager();
            try (PreparedStatement pstmt = dbm.prepareStatement(
                    connection,
                    "/sql/Card/InactivateNotIn.sql",
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
