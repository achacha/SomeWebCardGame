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

public class EncounterArchiveDboFactory extends BaseDboFactory<EncounterArchiveDbo> {
    public EncounterArchiveDboFactory() {
        super(EncounterArchiveDbo.class);
    }

    /**
     * Get a list of encounters for a given adventure archive
     * @param adventureArchiveId long
     * @return List of items (never null)
     */
    @Nonnull
    public List<EncounterArchiveDbo> getByAdventureId(Connection connection, long adventureArchiveId) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        List<EncounterArchiveDbo> encounters = new ArrayList<>();
        try (
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/EncounterArchive/SelectByAdventureArchiveId.sql",
                        p -> p.setLong(1, adventureArchiveId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                encounters.add(createFromResultSet(connection, rs));
            }
        } catch (Exception sqle) {
            LOGGER.error("Failed to find encounters for adventureArchiveId={}", adventureArchiveId, sqle);
        }
        return encounters;
    }
}
