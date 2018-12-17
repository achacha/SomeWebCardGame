package org.achacha.base.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.JdbcSession;
import org.achacha.base.global.Global;
import org.achacha.base.security.SecurityHelper;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class LoginUserDboFactory extends BaseDboFactory<LoginUserDbo> {
    public LoginUserDboFactory() {
        super(LoginUserDbo.class);
    }

    /**
     * Find login by email
     *
     * @param emailToFind String
     * @return LoginUserDbo or null if not found
     */
    @Nullable
    public LoginUserDbo findByEmail(String emailToFind) {
        try (
                JdbcSession session = Global.getInstance().getDatabaseManager().executeSql(
                        null,
                        "/sql/Login/SelectByEmail.sql",
                        p -> p.setString(1, emailToFind)
                )
        ) {
            if (session.getResultSet().next()) {
                LoginUserDbo dbo = new LoginUserDbo();
                dbo.fromResultSet(session.getConnection(), session.getResultSet());
                return dbo;
            } else {
                LoginUserDbo.LOGGER.warn("Failed to find login id={}", emailToFind);
            }
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to find login", sqle);
        }
        return null;
    }

    /**
     * Delete a LoginUserDbo from the database.
     * This method is for testing purposes only. Deletes on non-transient data should instead flip deleted flag.
     * @param email String
     */
    public void deleteForRealByEmail(String email) {
        try (
                Connection connection = Global.getInstance().getDatabaseManager().getConnection();
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Login/DeleteByEmail.sql",
                        p -> p.setString(1, email)
                )
        ) {
            pstmt.executeUpdate();
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to executeSql login", sqle);
        }
    }

    /**
     * Login a user
     *
     * @param email Email/username
     * @param pwd Raw password which will get salted
     * @return LoginUserDbo if valid login or null otherwise
     */
    @Nullable
    public LoginUserDbo login(String email, String pwd) {
        try (
                JdbcSession session = Global.getInstance().getDatabaseManager().executeSql(
                        null,
                        "/sql/Login/Login.sql",
                        p -> {
                            p.setString(1, email);
                            p.setString(2, SecurityHelper.encodeSaltPassword(pwd, email));
                        }
                )
        ) {
            if (session.getResultSet().next()) {
                LoginUserDbo dbo = new LoginUserDbo();
                dbo.fromResultSet(session.getConnection(), session.getResultSet());
                return dbo;
            } else {
                LoginUserDbo.LOGGER.warn("Login failed=[{}]", email);
                }
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to login user", sqle);
        }
        return null;
    }

    /**
     * Allow superuser to impersonate another user
     *
     * @param email String
     * @return LoginUserDbo if valid user or null otherwise
     */
    @Nullable
    public LoginUserDbo impersonate(String email) {
        CallContext context = CallContextTls.get();

        LoginUserDbo currentLogin = Preconditions.checkNotNull(context.getLogin());
        if (!currentLogin.superuser) {
            LoginUserDbo.LOGGER.error("Only superuser can impersonate another user, this functionality should never be allowed for anyone else");
            return null;
        }

        try (
                JdbcSession session = Global.getInstance().getDatabaseManager().executeSql(
                        null,
                        "/sql/Login/Impersonate.sql",
                        p -> p.setString(1, email)
                )
        ) {
            if (session.getResultSet().next()) {
                LoginUserDbo dbo = new LoginUserDbo();
                dbo.fromResultSet(session.getConnection(), session.getResultSet());

                // Set current login as imperesonator and set dbo as current login
                dbo.setImpersonator(currentLogin);
                context.setLogin(dbo);
                LOGGER.debug("Impersonate {} by superuser {}", email, currentLogin.getEmail());
                return dbo;
            } else {
                LoginUserDbo.LOGGER.warn("Login to impersonate email={} by playerId={}", email, context.getLogin().getId());
            }
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to login user", sqle);
        }
        return null;
    }

    /**
     * Save last_login_on timestamp to now
     */
    public void touch(LoginUserDbo dbo) {
        try (
                Connection connection = Global.getInstance().getDatabaseManager().getConnection();
                PreparedStatement pstmt = Global.getInstance().getDatabaseManager().prepareStatement(
                        connection,
                        "/sql/Login/UpdateLastLoginOn.sql",
                        p -> p.setLong(1, dbo.getId())
                )
        ) {
            pstmt.executeUpdate();
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to update last_login_on timestamp", sqle);
        }
    }

}
