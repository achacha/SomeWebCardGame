package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EncounterDboFactory extends BaseDboFactory<EncounterDbo> {
    public EncounterDboFactory(Class<EncounterDbo> clz) {
        super(clz);
    }

    /**
     * Get a list of encounters for a given adventure
     * @param adventureId long
     * @return List of items (never null)
     */
    @Nonnull
    public List<EncounterDbo> getEncountersForAdventure(long adventureId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
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
