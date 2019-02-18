package org.achacha.webcardgame.game.tick;

import com.google.common.base.Preconditions;
import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.dbo.AdventureArchiveDbo;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.achacha.webcardgame.game.dbo.AdventureDboFactory;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.logic.EncounterProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Tickable handler for AdventureDbo
 */
public class Adventure implements Tickable {
    transient private static final Logger LOGGER = LogManager.getLogger(Adventure.class);

    private final PlayerDbo player;
    private final AdventureDbo adventure;
    private final Iterator<EncounterDbo> currentEncounter;

    public Adventure(PlayerDbo playerDbo, AdventureDbo adventureDbo) {
        Preconditions.checkState(adventureDbo.getPlayerId() == playerDbo.getId());
        this.adventure = adventureDbo;
        this.player = playerDbo;
        currentEncounter = adventureDbo.getEncounters().iterator();
    }

    public AdventureDbo getAdventure() {
        return adventure;
    }

    @Override
    public void tick() {
        LOGGER.debug("+++Tick start for adventure={}", this);
        if (currentEncounter.hasNext()) {
            EncounterDbo encounter = currentEncounter.next();
            EncounterProcessor processor = new EncounterProcessor(player, encounter);
            processor.doEncounter();
        }
        else {
            LOGGER.debug("Tick called after complete, no-op");
        }
        LOGGER.debug("---Tick end for adventure={}", this);
    }

    @Override
    public boolean isComplete() {
        return !currentEncounter.hasNext();
    }

    @Override
    public void onComplete() {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            // Archive adventure and delete it
            AdventureDboFactory adventureFactory = Global.getInstance().getDatabaseManager().getFactory(AdventureDbo.class);
            AdventureArchiveDbo adventureArchive = new AdventureArchiveDbo(adventure);
            adventureArchive.insert(connection);
            adventureFactory.deleteById(connection, adventure.getId());
            connection.commit();
            LOGGER.debug("Adventure completed, archived adventure={} to adventureArchive={}", adventure, adventureArchive);
        }
        catch(SQLException e) {
            LOGGER.error("Failed to archive adventure="+adventure+" for player="+player, e);
        }
    }
}
