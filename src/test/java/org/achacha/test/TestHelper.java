package org.achacha.test;

import org.achacha.base.global.Global;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TestHelper {
    /**
     * Creates a player owned by TestDataConstants.JUNIT_USER_LOGINID and persists it
     * Contains 3 cards
     * @param playerNameBase String base of player name will be appended with random #
     * @return PlayerDbo
     */
    public static PlayerDbo createNewTestPlayer(String playerNameBase) throws SQLException {
        String playerName = playerNameBase + "_" + LocalDateTime.now().toString();
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = PlayerDbo.builder(TestDataConstants.JUNIT_USER_LOGINID, playerName)
                    .withCard(CardDbo.builder()
                            .withType(CardType.Human)
                            .withName(playerNameBase+"_card0")
                            .build())
                    .withCard(CardDbo.builder()
                            .withType(CardType.Human)
                            .withName(playerNameBase+"_card1")
                            .build())
                    .withCard(CardDbo.builder()
                            .withType(CardType.Human)
                            .withName(playerNameBase+"_card2")
                            .build())
                    .build();

            // Insert call will correctly propagate player ID to child classes
            player.insert(connection);
            connection.commit();

            System.out.println("Created new player="+player);

            return player;
        }
    }
}
