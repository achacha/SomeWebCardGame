package org.achacha.base;

import org.achacha.base.global.Global;
import org.achacha.base.global.UnitTestGlobal;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class BaseInitializedTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseInitializedTest.class);

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
