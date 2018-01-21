package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EncounterDboFactory extends BaseDboFactory {
    private static final Logger LOGGER = LogManager.getLogger(ItemDbo.class);

    /**
     * Get a list of encounters for a given adventure
     * @param adventureId long
     * @return List of items (never null)
     */
    @Nonnull
    public static List<EncounterDbo> getEncountersForAdventure(long adventureId) {
        List<EncounterDbo> encounters = new ArrayList<>();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Encounter/SelectByAdventureId.sql",
                        p -> p.setLong(1, adventureId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                EncounterDbo dbo = new EncounterDbo();
                dbo.fromResultSet(rs);
                encounters.add(dbo);
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find encounters for adventureId={}", adventureId, sqle);
        }
        return encounters;
    }
}
