package org.achacha.base.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.UnitTestGlobal;
import org.junit.Assert;
import org.junit.Test;

public class TestLoginDbo extends BaseInitializedTest {
    @Test
    public void testDboRead() {
        LoginUserDbo loginById = LoginUserDboFactory.findById(UnitTestGlobal.JUNIT_LOGINID);
        Assert.assertNotNull(loginById);
        Assert.assertEquals(UnitTestGlobal.JUNIT_LOGINID, loginById.getId());
        Assert.assertEquals(UnitTestGlobal.JUNIT_EMAIL, loginById.getEmail());

        LoginUserDbo loginByEmail = LoginUserDboFactory.findByEmail(UnitTestGlobal.JUNIT_EMAIL);
        Assert.assertNotNull(loginByEmail);
        Assert.assertEquals(UnitTestGlobal.JUNIT_EMAIL, loginByEmail.getEmail());
        Assert.assertEquals(UnitTestGlobal.JUNIT_LOGINID, loginByEmail.getId());
    }
}
