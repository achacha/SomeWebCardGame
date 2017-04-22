package org.achacha.base;

import org.achacha.base.global.Global;
import org.achacha.base.global.UnitTestGlobal;
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

        // Mock ServletContext
        ServletContext sc = Mockito.mock(ServletContext.class);
        Mockito.when(sc.getContextPath()).thenReturn("");

        // Mock ServletContextEvent
        ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
        Mockito.when(sce.getServletContext()).thenReturn(sc);

        Global.setInstance(new UnitTestGlobal());
        Global.getInstance().init(sce);
    }

    @AfterClass
    public static void deinit() {
        LOGGER.info("---INIT");
    }
}
