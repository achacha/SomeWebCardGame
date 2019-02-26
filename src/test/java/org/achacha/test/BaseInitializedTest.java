package org.achacha.test;

import org.achacha.base.global.Global;
import org.achacha.base.global.GlobalForTest;
import org.achacha.webcardgame.game.data.CardType;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;
import org.achacha.webcardgame.game.dbo.PlayerDboFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseInitializedTest {
    private static final Logger LOGGER = LogManager.getLogger(BaseInitializedTest.class);

    @BeforeAll
    public static void init() {
        LOGGER.debug("+++INIT");

        if (Global.getInstance() == null) {
            LOGGER.info("Global begin initialized");

            // Display classpath jars
            //ClasspathHelper.forJavaClassPath().forEach(System.out::println);

            // Mock ServletContext
            ServletContext sc = Mockito.mock(ServletContext.class);
            Mockito.when(sc.getContextPath()).thenReturn("");

            // Mock ServletContextEvent
            ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
            Mockito.when(sce.getServletContext()).thenReturn(sc);

            Global.setInstance(new GlobalForTest());
            Global.getInstance().init(sce);
        }
        else {
            LOGGER.debug("Global already initialized, skipping init");
        }
    }

    @AfterAll
    public static void deinit() {
        LOGGER.debug("---INIT");
    }

    /**
     * Creates a player owned by TestDataConstants.JUNIT_USER_LOGINID and persists it
     * Contains 3 cards
     * @param playerName String
     * @return PlayerDbo
     */
    public PlayerDbo createNewTestPlayer(String playerName) throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            PlayerDbo player = PlayerDbo.builder(TestDataConstants.JUNIT_USER_LOGINID, playerName)
                    .withCard(CardDbo.builder()
                            .withType(CardType.Human)
                            .withName(playerName+"_card0")
                            .build())
                    .withCard(CardDbo.builder()
                            .withType(CardType.Human)
                            .withName(playerName+"_card1")
                            .build())
                    .withCard(CardDbo.builder()
                            .withType(CardType.Human)
                            .withName(playerName+"_card2")
                            .build())
                    .build();

            // Insert call will correctly propagate player ID to child classes
            player.insert(connection);
            connection.commit();

            return player;
        }
    }

    // TODO: Test this
    public void deletePlayer(Connection connection, PlayerDbo player) throws SQLException {
        Global.getInstance().getDatabaseManager().getFactory(PlayerDbo.class).deleteById(connection, player.getId());

        // Verify deleted
        PlayerDbo playerDeleted = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(connection, TestDataConstants.JUNIT_USER_LOGINID, TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(playerDeleted);
    }
}
