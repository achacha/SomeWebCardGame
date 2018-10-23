package org.achacha.base.dbo;

import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class LoginAttrDboFactory extends BaseDboFactory<LoginAttrDbo> {
    public LoginAttrDboFactory() {
        super(LoginAttrDbo.class);
    }

    /**
     * Find by playerId and name
     *
     * @param loginId long
     * @param name String
     * @return true if deleted or false if it did not exist in the first place
     */
    public LoginAttrDbo findByLoginIdAndName(long loginId, String name) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/LoginAttr/FindByLoginIdAndName.sql",
                        p -> {
                            p.setLong(1, loginId);
                            p.setString(2, name);
                        }
                );
                ResultSet rs = pstmt.executeQuery()
        ) {
            if (rs.next()) {
                LoginAttrDbo dbo = new LoginAttrDbo();
                dbo.fromResultSet(rs);
                return dbo;
            } else {
                LoginAttrDbo.LOGGER.debug("Failed to find login_attr playerId={}  name={}", loginId, name);
            }
        } catch (Exception e) {
            LoginAttrDbo.LOGGER.error("Failed to find login_attr, playerId={}  name={}", loginId, name, e);
        }
        return null;
    }

    /**
     * Find login attributes by user id
     *
     * @param userId long
     * @return Collection of LoginDbo
     */
    @Nonnull
    public Collection<LoginAttrDbo> findByLoginId(long userId) {
        ArrayList<LoginAttrDbo> attrs = new ArrayList<>();
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/LoginAttr/SelectByLoginId.sql",
                        p -> p.setLong(1, userId));
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                LoginAttrDbo dbo = new LoginAttrDbo();
                dbo.fromResultSet(rs);
                attrs.add(dbo);
            }
        } catch (Exception e) {
            LoginAttrDbo.LOGGER.error("Failed to find login_attr for playerId="+userId, e);
        }
        return attrs;
    }

    /**
     * Delete by playerId and name
     *
     * @param loginId long
     * @param name String
     * @return true if deleted or false if it did not exist in the first place
     */
    public boolean deleteByLoginIdAndName(long loginId, String name) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/LoginAttr/DeleteByLoginIdAndName.sql",
                        p -> {
                            p.setLong(1, loginId);
                            p.setString(2, name);
                        }
                )
        ) {
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected != 1) {
                LoginAttrDbo.LOGGER.debug("Unable to delete login_attr, playerId={}  name={}", loginId, name);
                return false;
            }
            else
                return true;
        } catch (Exception e) {
            LoginAttrDbo.LOGGER.error("Failed to delete login_attr, playerId={}  name={}", loginId, name, e);
        }
        return false;
    }
}
