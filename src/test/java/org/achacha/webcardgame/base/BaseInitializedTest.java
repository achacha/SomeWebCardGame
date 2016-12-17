package org.achacha.webcardgame.base;

import org.achacha.webcardgame.db.Factory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class BaseInitializedTest {

    @BeforeClass
    public static void init() {
        System.out.println("+++INIT");
        Factory.getInstance().init();
    }

    @AfterClass
    public static void deinit() {
        System.out.println("---INIT");
        Factory.getInstance().destroy();
    }
}
