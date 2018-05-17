package org.achacha.base.dbo;

import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.Assert;
import org.junit.Test;

public class LoginDboTest extends BaseInitializedTest {
    @Test
    public void testDboRead() {
        LoginUserDbo loginById = LoginUserDboFactory.findById(TestDataConstants.JUNIT_LOGINID);
        Assert.assertNotNull(loginById);
        Assert.assertEquals(TestDataConstants.JUNIT_LOGINID, loginById.getId());
        Assert.assertEquals(TestDataConstants.JUNIT_EMAIL, loginById.getEmail());

        LoginUserDbo loginByEmail = LoginUserDboFactory.findByEmail(TestDataConstants.JUNIT_EMAIL);
        Assert.assertNotNull(loginByEmail);
        Assert.assertEquals(TestDataConstants.JUNIT_EMAIL, loginByEmail.getEmail());
        Assert.assertEquals(TestDataConstants.JUNIT_LOGINID, loginByEmail.getId());
    }
}
