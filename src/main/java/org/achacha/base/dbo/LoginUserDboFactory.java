package org.achacha.base.dbo;

import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.JdbcTuple;
import org.achacha.base.global.Global;
import org.achacha.base.security.SecurityHelper;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class LoginUserDboFactory extends BaseDboFactory {
    /**
     * Find login by id
     *
     * @param idToFind int
     * @return LoginUserDbo or null if not found
     */
    @Nullable
    public static LoginUserDbo findById(long idToFind) {
        try (
                JdbcTuple triple = Global.getInstance().getDatabaseManager().executeSql(
                        "/sql/Login/SelectById.sql",
                        p -> p.setLong(1, idToFind)
                )
        ) {
            if (triple.getResultSet().next()) {
                LoginUserDbo dbo = new LoginUserDbo();
                dbo.fromResultSet(triple.getResultSet());
                return dbo;
            } else {
                LoginUserDbo.LOGGER.warn("Failed to find login id={}", idToFind);
            }
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to find login", sqle);
        }

        return null;
    }

    /**
     * Find login by email
     *
     * @param emailToFind String
     * @return LoginUserDbo or null if not found
     */
    @Nullable
    public static LoginUserDbo findByEmail(String emailToFind) {
        try (
                JdbcTuple triple = Global.getInstance().getDatabaseManager().executeSql(
                        "/sql/Login/SelectByEmail.sql",
                        p -> p.setString(1, emailToFind)
                )
        ) {
            if (triple.getResultSet().next()) {
                LoginUserDbo dbo = new LoginUserDbo();
                dbo.fromResultSet(triple.getResultSet());
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
     */
    public static void deleteForRealByEmail(String email) {
        DatabaseManager dm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dm.getConnection();
                PreparedStatement pstmt = dm.prepareStatement(
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
    public static LoginUserDbo login(String email, String pwd) {
        try (
                JdbcTuple triple = Global.getInstance().getDatabaseManager().executeSql(
                        "/sql/Login/Login.sql",
                        p -> {
                            p.setString(1, email);
                            p.setString(2, SecurityHelper.encodeSaltPassword(pwd, email));
                        }
                )
        ) {
            if (triple.getResultSet().next()) {
                LoginUserDbo dbo = new LoginUserDbo();
                dbo.fromResultSet(triple.getResultSet());
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
    public static LoginUserDbo impersonate(String email) {
        CallContext context = CallContextTls.get();

        if (!context.getLogin().superuser) {
            LoginUserDbo.LOGGER.error("Only superuser can impersonate another user, this functionality should never be allowed for anyone else");
            return null;
        }

        try (
                JdbcTuple triple = Global.getInstance().getDatabaseManager().executeSql(
                        "/sql/Login/Impersonate.sql",
                        p -> p.setString(1, email)
                )
        ) {
            if (triple.getResultSet().next()) {
                LoginUserDbo dbo = new LoginUserDbo();
                dbo.fromResultSet(triple.getResultSet());
                return dbo;
            } else {
                LoginUserDbo.LOGGER.warn("Login to impersonate email={} by loginId={}", email, context.getLogin().getId());
            }
        } catch (Exception sqle) {
            LoginUserDbo.LOGGER.error("Failed to login user", sqle);
        }
        return null;
    }
}
