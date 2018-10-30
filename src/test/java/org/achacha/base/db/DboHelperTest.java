package org.achacha.base.db;

import org.achacha.base.dbo.EventLogDbo;
import org.achacha.base.dbo.LoginAttrDbo;
import org.achacha.base.dbo.LoginPersonaDbo;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.test.TestSimpleDbo;
import org.achacha.webcardgame.game.dbo.AdventureDbo;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DboHelperTest {
    @Test
    void testGetAllDboClassNames() {
        Set<Class<? extends BaseDbo>> classes = DboHelper.getAllDboClasses();
        assertTrue(classes.contains(LoginUserDbo.class));
        assertTrue(classes.contains(LoginPersonaDbo.class));
        assertTrue(classes.contains(LoginAttrDbo.class));
        assertTrue(classes.contains(EventLogDbo.class));
    }

    @Test
    void testGetAllCachedDboClassNames() {
        Set<Class<? extends BaseIndexedDbo>> classes = DboHelper.getAllCachedDboClasses();
        assertFalse(classes.contains(LoginUserDbo.class));
    }

    @Test
    void testGetIndexedDboClasses() {
        Set<Class<? extends BaseIndexedDbo>> classes = DboHelper.getIndexedDboClasses();
        assertFalse(classes.contains(TestSimpleDbo.class));   // Test classes should not be included
        assertTrue(classes.contains(LoginUserDbo.class));
        assertTrue(classes.contains(AdventureDbo.class));
    }
}
