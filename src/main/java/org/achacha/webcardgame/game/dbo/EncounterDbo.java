package org.achacha.webcardgame.game.dbo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.db.DboDataHelper;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.webcardgame.game.data.CardType;
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
@Table(schema="public", name="encounter")
public class EncounterDbo extends BaseDbo {
    transient private static final Logger LOGGER = LogManager.getLogger(EncounterDbo.class);

    /** id */
    protected long id;

    /** Associated adventure */
    protected long adventureId;

    /** Enemies in this encounter */
    protected List<CardDbo> enemies;

    /** Result from encounter */
    protected EncounterProcessor.Result result = EncounterProcessor.Result.None;

    public static Builder builder(AdventureDbo adventure) {
        return new Builder(adventure);
    }

    public static class Builder {
        private final AdventureDbo adventure;
        private EncounterDbo encounter = new EncounterDbo();

        Builder(AdventureDbo adventure) {
            this.adventure = adventure;
            encounter.adventureId = adventure.getId();
            encounter.enemies = new ArrayList<>();
        }

        public Builder withCard(CardDbo card) {
            encounter.enemies.add(card);
            return this;
        }

        public Builder withGeneratedCard(CardType enemyType, int level, String name) {
            encounter.enemies.add(CardDbo.builder(adventure.playerId)
                    .withType(enemyType)
                    .withLevel(level)
                    .withName(name)
                    .build()
            );
            return this;
        }

        /** Generate random name for type provided */
        public Builder withGeneratedCard(CardType enemyType, int level) {
            encounter.enemies.add(
                    CardDbo.builder(adventure.playerId)
                            .withTypeAndRandomName(enemyType)
                            .withLevel(level)
                            .build()
            );
            return this;
        }

        public EncounterDbo build() {
            return encounter;
        }
    }

    public EncounterDbo() {
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.adventureId = rs.getLong("adventure__id");

        // Enemies CardDbo objects stored as JSON array
        JsonArray ary = JsonHelper.fromString(rs.getString("enemy_cards")).getAsJsonArray();
        enemies = DboDataHelper.from(CardDbo.class, ary);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    public long getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(long adventureId) {
        this.adventureId = adventureId;
    }

    public List<CardDbo> getEnemies() {
        return enemies;
    }

    public EncounterProcessor.Result getResult() {
        return result;
    }

    public void setResult(EncounterProcessor.Result result) {
        this.result = result;
    }

    @Override
    public void insert(Connection connection) throws SQLException {
        try (
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(connection,
                        "/sql/Encounter/Insert.sql",
                        p-> {
                            p.setLong(1, adventureId);

                            JsonArray ary = new JsonArray();
                            for (CardDbo enemy : this.enemies) {
                                JsonObject toJsonObject = enemy.toJsonObject();
                                ary.add(toJsonObject);
                            }
                            PGobject pgo = new PGobject();
                            pgo.setType("json");
                            pgo.setValue(ary.toString());
                            p.setObject(2, pgo);

                            p.setInt(3, result.ordinal());
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                // Returns id of the newly inserted item
                this.id = rs.getLong(1);
            }
            else {
                LOGGER.error("Failed to insert new encounter={}", this);
                throw new SQLException("Failed to insert encounter=" + this);
            }
        }
    }
}
