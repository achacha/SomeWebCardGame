package org.achacha.test;

import org.achacha.base.global.Global;
import org.achacha.base.global.GlobalForTest;
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
     * @return PlayerDbo
     */
    public PlayerDbo createNewTestPlayer() throws SQLException {
        PlayerDbo player = PlayerDbo.builder(TestDataConstants.JUNIT_USER_LOGINID).build();

        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            player.insert(connection);
            connection.commit();
        }

        return player;
    }

    public void deletePlayer(PlayerDbo player) throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            Global.getInstance().getDatabaseManager().getFactory(PlayerDbo.class).deleteById(connection, player.getId());
            connection.commit();
        }

        // Verify deleted
        PlayerDbo playerDeleted = Global.getInstance().getDatabaseManager().<PlayerDboFactory>getFactory(PlayerDbo.class).getByLoginIdAndPlayerId(TestDataConstants.JUNIT_USER_LOGINID, TestDataConstants.JUNIT_PLAYER__ID);
        assertNotNull(playerDeleted);
    }
}
