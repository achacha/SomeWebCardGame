package org.achacha.base.dbo;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestDataConstants;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginDboTest extends BaseInitializedTest {
    private LoginUserDboFactory factory = Global.getInstance().getDatabaseManager().getFactory(LoginUserDbo.class);

    @Test
    void testDboRead() throws SQLException {
        try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
            LoginUserDbo loginById = factory.getById(connection, TestDataConstants.JUNIT_USER_LOGINID);
            assertNotNull(loginById);
            assertEquals(TestDataConstants.JUNIT_USER_LOGINID, loginById.getId());
            assertEquals(TestDataConstants.JUNIT_USER_EMAIL, loginById.getEmail());

            LoginUserDbo loginByEmail = factory.findByEmail(TestDataConstants.JUNIT_USER_EMAIL);
            assertNotNull(loginByEmail);
            assertEquals(TestDataConstants.JUNIT_USER_EMAIL, loginByEmail.getEmail());
            assertEquals(TestDataConstants.JUNIT_USER_LOGINID, loginByEmail.getId());
        }
    }
}
