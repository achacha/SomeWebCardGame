package org.achacha.webcardgame.game.logic;

import com.google.common.base.Preconditions;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.dbo.AdventureArchiveDbo;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.sql.Connection;

public class AdventureLogic {
    private static final Logger LOGGER = LogManager.getLogger(AdventureLogic.class);

    /**
     * Simulate adventure, create archive and delete active adventure
     * @param player PlayerDbo
     * @param adventure AdventureDbo
     * @return AdventureArchiveDbo or null if failed
     */
    @Nullable
    public static AdventureArchiveDbo simulateAdventure(Connection connection, PlayerDbo player, AdventureDbo adventure) throws Exception {
        Preconditions.checkState(player.getId() == adventure.getPlayerId());

        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        // Simulate encounters
        adventure.getEncounters().forEach(encounter -> {
            EncounterProcessor processor = new EncounterProcessor(player, encounter);
            processor.doEncounter();
        });

        // Archive adventure and delete it
        AdventureDboFactory adventureFactory = dbm.getFactory(AdventureDbo.class);
        AdventureArchiveDbo adventureArchive = new AdventureArchiveDbo(adventure);
        adventureArchive.insert(connection);
        adventureFactory.deleteById(connection, adventure.getId());
        connection.commit();
        return adventureArchive;
    }
}
