package org.achacha.test;

import org.achacha.base.global.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class BaseInitializedTest {
    private static final Logger LOGGER = LogManager.getLogger(BaseInitializedTest.class);

    @BeforeClass
    public static void init() {
        LOGGER.info("+++INIT");

        //TODO: check if this can be initialized only once
        if (Global.getInstance() == null) {
            LOGGER.info("Global begin initialized");

            // Mock ServletContext
            ServletContext sc = Mockito.mock(ServletContext.class);
            Mockito.when(sc.getContextPath()).thenReturn("");

            // Mock ServletContextEvent
            ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
            Mockito.when(sce.getServletContext()).thenReturn(sc);

            Global.setInstance(new UnitTestGlobal());
            Global.getInstance().init(sce);
        }
        else {
            LOGGER.info("Global already initialized, skipping init");
        }
    }

    @AfterClass
    public static void deinit() {
        LOGGER.info("---INIT");
    }
}
