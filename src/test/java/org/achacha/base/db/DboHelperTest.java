package org.achacha.base.db;

import org.achacha.base.dbo.EventLogDbo;
import org.achacha.base.dbo.LoginAttrDbo;
import org.achacha.base.dbo.LoginPersonaDbo;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.test.TestSimpleDbo;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class DboHelperTest {
    @Test
    public void testGetAllDboClassNames() {
        Set<Class<? extends BaseDbo>> classes = DboHelper.getAllDboClasses();
        Assert.assertTrue(classes.contains(LoginUserDbo.class));
        Assert.assertTrue(classes.contains(LoginPersonaDbo.class));
        Assert.assertTrue(classes.contains(LoginAttrDbo.class));
        Assert.assertTrue(classes.contains(EventLogDbo.class));
    }

    @Test
    public void testGetAllCachedDboClassNames() {
        Set<Class<? extends BaseIndexedDbo>> classes = DboHelper.getAllCachedDboClasses();
        Assert.assertFalse(classes.contains(LoginUserDbo.class));
    }

    @Test
    public void testGetIndexedDboClasses() {
        Set<Class<? extends BaseIndexedDbo>> classes = DboHelper.getIndexedDboClasses();
        Assert.assertFalse(classes.contains(TestSimpleDbo.class));   // Test classes should not be included
        Assert.assertTrue(classes.contains(LoginUserDbo.class));
        Assert.assertTrue(classes.contains(AdventureDbo.class));
    }
}
