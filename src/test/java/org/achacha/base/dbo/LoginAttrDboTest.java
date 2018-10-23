package org.achacha.base.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class LoginAttrDboTest extends BaseInitializedTest {
    @Test
    public void testDboRead() {
        LoginAttrDboFactory factoryAttr = Global.getInstance().getDatabaseManager().getFactory(LoginAttrDbo.class);

        // Single attribute by id
        LoginAttrDbo dbo = factoryAttr.byId(1);
        Assert.assertNotNull(dbo);
        Assert.assertEquals(1, dbo.getId());
        Assert.assertEquals("color", dbo.getName());
        Assert.assertEquals("red", dbo.getValue());

        // Attributes by playerId
        LoginUserDbo login = Global.getInstance().getDatabaseManager().<LoginUserDboFactory>getFactory(LoginUserDbo.class).byId(TestDataConstants.JUNIT_LOGINID);
        Assert.assertNotNull(login);
        Collection<LoginAttrDbo> attrs = factoryAttr.findByLoginId(login.getId());
        Assert.assertEquals(3, attrs.size());
        Assert.assertTrue(attrs.contains(dbo));
    }

    @Test
    public void testCreateDelete() throws Exception {
        LoginAttrDboFactory factoryAttr = Global.getInstance().getDatabaseManager().getFactory(LoginAttrDbo.class);

        // Pre-delete
        factoryAttr.deleteByLoginIdAndName(TestDataConstants.JUNIT_LOGINID, "test.create");

        // Create new object and save to DB
        LoginAttrDbo dbo = new LoginAttrDbo();
        dbo.setName("test.create");
        dbo.setValue("inserted");
        dbo.setLoginId(TestDataConstants.JUNIT_LOGINID);
        Assert.assertEquals(0, dbo.getId());
        dbo.save();
        Assert.assertNotEquals(0, dbo.getId());

        // Verify exists
        dbo = factoryAttr.byId(dbo.getId());
        Assert.assertNotNull(dbo);
        Assert.assertEquals(TestDataConstants.JUNIT_LOGINID, dbo.getLoginId());
        Assert.assertEquals("test.create", dbo.getName());
        Assert.assertEquals("inserted", dbo.getValue());

        // Update and reload to verify update worked
        dbo.setValue("updated");
        dbo.save();
        dbo = factoryAttr.byId(dbo.getId());
        Assert.assertNotNull(dbo);
        Assert.assertEquals(TestDataConstants.JUNIT_LOGINID, dbo.getLoginId());
        Assert.assertEquals("test.create", dbo.getName());
        Assert.assertEquals("updated", dbo.getValue());

        // Delete and verify it is gone
        factoryAttr.deleteById(dbo.getId());
        Assert.assertNull(factoryAttr.byId(dbo.getId()));
    }
}
