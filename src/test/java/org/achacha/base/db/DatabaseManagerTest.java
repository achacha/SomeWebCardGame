package org.achacha.base.db;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.junit.Assert;
import org.junit.Test;

public class DatabaseManagerTest extends BaseInitializedTest {
    @Test
    public void testInitialization() {
        Assert.assertNotNull(Global.getInstance());
        DatabaseManager dm = Global.getInstance().getDatabaseManager();
        Assert.assertNotNull(dm);
    }
}
