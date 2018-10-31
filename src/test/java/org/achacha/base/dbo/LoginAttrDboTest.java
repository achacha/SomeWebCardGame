package org.achacha.base.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttrDboTest extends BaseInitializedTest {
    @Test
    void testDboRead() {
        LoginAttrDboFactory factoryAttr = Global.getInstance().getDatabaseManager().getFactory(LoginAttrDbo.class);

        // Single attribute by id
        LoginAttrDbo dbo = factoryAttr.getById(1);
        assertNotNull(dbo);
        assertEquals(1, dbo.getId());
        assertEquals("color", dbo.getName());
        assertEquals("red", dbo.getValue());

        // Attributes by playerId
        LoginUserDbo login = Global.getInstance().getDatabaseManager().<LoginUserDboFactory>getFactory(LoginUserDbo.class).getById(TestDataConstants.JUNIT_USER_LOGINID);
        assertNotNull(login);
        Collection<LoginAttrDbo> attrs = factoryAttr.findByLoginId(login.getId());
        assertEquals(3, attrs.size());
        assertTrue(attrs.contains(dbo));
    }

    @Test
    void testCreateDelete() throws Exception {
        LoginAttrDboFactory factoryAttr = Global.getInstance().getDatabaseManager().getFactory(LoginAttrDbo.class);

        // Pre-delete
        factoryAttr.deleteByLoginIdAndName(TestDataConstants.JUNIT_USER_LOGINID, "test.create");

        // Create new object and save to DB
        LoginAttrDbo dbo = new LoginAttrDbo();
        dbo.setName("test.create");
        dbo.setValue("inserted");
        dbo.setLoginId(TestDataConstants.JUNIT_USER_LOGINID);
        assertEquals(0, dbo.getId());
        dbo.save();
        assertNotEquals(0, dbo.getId());

        // Verify exists
        dbo = factoryAttr.getById(dbo.getId());
        assertNotNull(dbo);
        assertEquals(TestDataConstants.JUNIT_USER_LOGINID, dbo.getLoginId());
        assertEquals("test.create", dbo.getName());
        assertEquals("inserted", dbo.getValue());

        // Update and reload to verify update worked
        dbo.setValue("updated");
        dbo.save();
        dbo = factoryAttr.getById(dbo.getId());
        assertNotNull(dbo);
        assertEquals(TestDataConstants.JUNIT_USER_LOGINID, dbo.getLoginId());
        assertEquals("test.create", dbo.getName());
        assertEquals("updated", dbo.getValue());

        // Delete and verify it is gone
        factoryAttr.deleteById(dbo.getId());
        assertNull(factoryAttr.getById(dbo.getId()));
    }
}
