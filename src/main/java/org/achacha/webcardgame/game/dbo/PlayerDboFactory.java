package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PlayerDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(PlayerDbo.class);

    /**
     * Get all PlayerDbo objects associated with a login id
     * @param loginId long id
     * @return List of PlayerDbo, never null
     */
    @Nonnull
    public static List<PlayerDbo> getByLoginId(long loginId) {
        List<PlayerDbo> dbos = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Player/SelectByLoginId.sql",
                        p -> p.setLong(1, loginId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                PlayerDbo dbo = new PlayerDbo();
                dbo.fromResultSet(rs);
                dbos.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find players for loginId={}", loginId, sqle);
        }
        return dbos;
    }

    /**
     * Get all PlayerDbo objects associated with a login id
     * @param loginId long login id
     * @param playerId long player id
     * @return PlayerDbo or null
     */
    @Nullable
    public static PlayerDbo getByLoginIdAndPlayerId(long loginId, long playerId) {
        PlayerDbo dbo = null;
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Player/SelectByPlayerId.sql",
                        p -> {
                            p.setLong(1, playerId);
                            p.setLong(2, loginId);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                dbo = new PlayerDbo();
                dbo.fromResultSet(rs);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find player for playerId={} and loginId={}", playerId, loginId, sqle);
        }
        return dbo;
    }
}
