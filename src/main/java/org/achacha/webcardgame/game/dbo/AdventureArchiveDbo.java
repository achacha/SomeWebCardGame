package org.achacha.webcardgame.game.dbo;

import org.achacha.base.db.BaseDbo;
import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adventure that completed
 */
@Table(schema="public", name="adventure_archive")
public class AdventureArchiveDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(AdventureArchiveDbo.class);

    /** ID */
    private long id;

    /** Original ID */
    private long originalId;

    /** Original create time */
    private Timestamp originalCreated;

    /** Player ID */
    private long playerId;

    /** Title */
    private String title;

    /**
     * Completed time
     * This will be set by the database using NOW() upon insert and returned and set on object
     * @see #insert(Connection)
     */
    private Timestamp completed;

    /** Encounters in this Adventure */
    private List<EncounterArchiveDbo> encounters;

    public AdventureArchiveDbo() {
    }

    public AdventureArchiveDbo(AdventureDbo originalAdventure) {
        this.originalId = originalAdventure.id;
        this.originalCreated = originalAdventure.created;
        this.playerId = originalAdventure.playerId;
        this.title = originalAdventure.title;
        this.encounters = originalAdventure.encounters.stream().map(EncounterArchiveDbo::new).collect(Collectors.toList());
    }

    @Override
    public long getId() {
        return id;
    }

    public long getOriginalId() {
        return originalId;
    }

    public Timestamp getOriginalCreated() {
        return originalCreated;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getCompleted() {
        return completed;
    }

    public List<EncounterArchiveDbo> getEncounters() {
        return encounters;
    }

    /**
     * Insert new adventure archive after it is completed
     * id and completed are set upon insert and set on object
     * @param connection Connection to reuse
     * @throws SQLException If unable to insert
     */
    @Override
    public void insert(Connection connection) throws SQLException {
        try(
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/AdventureArchive/Insert.sql",
                        p-> {
                            p.setLong(1, originalId);
                            p.setTimestamp(2, originalCreated);
                            p.setLong(3, playerId);
                            p.setString(4, title);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                id = rs.getLong(1);
                completed = rs.getTimestamp(2);
            }
            else {
                LOGGER.error("Failed to insert new archive adventure={}", this);
                throw new SQLException("Failed to insert archive adventure="+this);
            }

        }

        // Insert encounters in archive
        for (EncounterArchiveDbo dbo : encounters) {
            dbo.setAdventureArchiveId(id);  // Set id from inserted
            dbo.insert(connection);
        }
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        id = rs.getLong("id");
        originalId = rs.getLong("original_id");
        originalCreated = rs.getTimestamp("original_created");
        playerId = rs.getLong("player__id");
        title = rs.getString("title");
        completed = rs.getTimestamp("completed");
        encounters = Global.getInstance().getDatabaseManager().<EncounterArchiveDboFactory>getFactory(EncounterArchiveDbo.class).getByAdventureId(connection, this.id);
    }
}
