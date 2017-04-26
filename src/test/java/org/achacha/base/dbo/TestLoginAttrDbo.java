package org.achacha.base.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.UnitTestGlobal;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class TestLoginAttrDbo extends BaseInitializedTest {
    @Test
    public void testDboRead() {
        // Single attribute by id
        LoginAttrDbo dbo = LoginAttrFactoryDbo.findById(1);
        Assert.assertNotNull(dbo);
        Assert.assertEquals(1, dbo.getId());
        Assert.assertEquals("color", dbo.getName());
        Assert.assertEquals("red", dbo.getValue());

        // Attributes by loginId
        LoginUserDbo login = LoginUserDboFactory.findById(UnitTestGlobal.JUNIT_LOGINID);
        Assert.assertNotNull(login);
        Collection<LoginAttrDbo> attrs = LoginAttrFactoryDbo.findByLoginId(login.getId());
        Assert.assertEquals(3, attrs.size());
        Assert.assertTrue(attrs.contains(dbo));
    }

    @Test
    public void testCreateDelete() throws Exception {
        // Pre-delete
        LoginAttrFactoryDbo.deleteByLoginIdAndName(UnitTestGlobal.JUNIT_LOGINID, "test.create");

        // Create new object and save to DB
        LoginAttrDbo dbo = new LoginAttrDbo();
        dbo.setName("test.create");
        dbo.setValue("inserted");
        dbo.setLoginId(UnitTestGlobal.JUNIT_LOGINID);
        Assert.assertEquals(0, dbo.getId());
        dbo.save();
        Assert.assertNotEquals(0, dbo.getId());

        // Verify exists
        dbo = LoginAttrFactoryDbo.findById(dbo.getId());
        Assert.assertNotNull(dbo);
        Assert.assertEquals(UnitTestGlobal.JUNIT_LOGINID, dbo.getLoginId());
        Assert.assertEquals("test.create", dbo.getName());
        Assert.assertEquals("inserted", dbo.getValue());

        // Update and reload to verify update worked
        dbo.setValue("updated");
        dbo.save();
        dbo = LoginAttrFactoryDbo.findById(dbo.getId());
        Assert.assertNotNull(dbo);
        Assert.assertEquals(UnitTestGlobal.JUNIT_LOGINID, dbo.getLoginId());
        Assert.assertEquals("test.create", dbo.getName());
        Assert.assertEquals("updated", dbo.getValue());

        // Delete and verify it is gone
        LoginAttrFactoryDbo.deleteById(dbo.getId());
        Assert.assertNull(LoginAttrFactoryDbo.findById(dbo.getId()));
    }
}
