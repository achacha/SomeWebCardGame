package org.achacha.webcardgame.game.dbo;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.db.DboDataHelper;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.webcardgame.game.logic.EncounterProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.PGobject;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encounter associated with adventure
 */
@Table(schema="public", name="encounter_archive")
public class EncounterArchiveDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(EncounterArchiveDbo.class);

    /** id */
    protected long id;

    /** Associated original adventure id */
    protected long adventureArchiveId;

    /** Enemies in this encounter */
    protected List<CardDbo> enemies;

    /** Result from encounter */
    private EncounterProcessor.Result result = EncounterProcessor.Result.None;

    public EncounterArchiveDbo() {
    }

    /**
     * Archive from active
     * @param encounter EncounterDbo
     */
    public EncounterArchiveDbo(EncounterDbo encounter) {
        this.enemies = new ArrayList<>(encounter.enemies);

        this.result = encounter.result;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setAdventureArchiveId(long adventureArchiveId) {
        this.adventureArchiveId = adventureArchiveId;
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.adventureArchiveId = rs.getLong("adventure_archive__id");

        int index = rs.getInt("result");
        this.result = EncounterProcessor.Result.values()[index];

        // Enemies CardDbo objects stored as JSON array
        JsonArray ary = JsonHelper.fromString(rs.getString("enemy_cards")).getAsJsonArray();
        enemies = DboDataHelper.from(CardDbo.class, ary);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public List<CardDbo> getEnemies() {
        return enemies;
    }

    public EncounterProcessor.Result getResult() {
        return result;
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        Preconditions.checkState(adventureArchiveId > 0);   // Should have been inserted and set on this
        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(connection,
                        "/sql/EncounterArchive/Insert.sql",
                        p-> {
                            p.setLong(1, adventureArchiveId);
                            p.setInt(2, result.ordinal());

                            JsonArray ary = new JsonArray();
                            enemies.stream().map(CardDbo::toJsonObject).forEach(ary::add);
                            PGobject pgo = new PGobject();
                            pgo.setType("json");
                            pgo.setValue(ary.toString());
                            p.setObject(3, pgo);
                         }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                // Returns id of the newly inserted item
                id = rs.getLong(1);
            }
            else {
                LOGGER.error("Failed to insert archive encounter={}", this);
                throw new SQLException("Failed to insert archive encounter=" + this);
            }
        }
    }
}
