package org.achacha.base;

import org.achacha.base.global.Global;
import org.achacha.base.global.UnitTestGlobal;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class BaseInitializedTest {

    @BeforeClass
    public static void init() {
        System.out.println("+++INIT");

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
        System.out.println("---INIT");
    }
}
