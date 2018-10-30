package org.achacha.base.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginDboTest extends BaseInitializedTest {
    private LoginUserDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(LoginUserDbo.class);
    @Test
    void testDboRead() {
        LoginUserDbo loginById = factory.getById(TestDataConstants.JUNIT_LOGINID);
        assertNotNull(loginById);
        assertEquals(TestDataConstants.JUNIT_LOGINID, loginById.getId());
        assertEquals(TestDataConstants.JUNIT_EMAIL, loginById.getEmail());

        LoginUserDbo loginByEmail = factory.findByEmail(TestDataConstants.JUNIT_EMAIL);
        assertNotNull(loginByEmail);
        assertEquals(TestDataConstants.JUNIT_EMAIL, loginByEmail.getEmail());
        assertEquals(TestDataConstants.JUNIT_LOGINID, loginByEmail.getId());
    }
}
