package org.achacha.base.dbo;


import com.google.gson.JsonObject;
import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.achacha.base.i18n.I18nHelper;
import org.achacha.base.security.SecurityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.TimeZone;

@Table(schema="public", name="login")
public class LoginUserDbo extends BaseIndexedDbo {
    protected static final Logger LOGGER = LogManager.getLogger(LoginUserDbo.class);

    /** Object database id */
    protected long id;

    /** User email */
    protected String email;

    /** User password */
    protected String pwd;

    /** User salt */
    protected String salt;

    /** User first name */
    protected String fname;

    /** User locale */
    protected Locale locale = Locale.getDefault();

    /** User timezone */
    protected TimeZone timezone = TimeZone.getDefault();

    /** User security level */
    protected SecurityLevel securityLevel = SecurityLevel.PUBLIC;

    /** Is user a superuser, flag overrides all security levels, use can have any level and this will override it */
    protected boolean superuser;

    /** Is user active */
    protected boolean active;

    /** Current impersinator of this user */
    transient protected LoginUserDbo impersonator;

    @Override
    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }

    public String getSalt() {
        return salt;
    }

    public String getFname() {
        return fname;
    }

    public Locale getLocale() {
        return locale;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public boolean isSuperuser() {
        return superuser;
    }

    public boolean isActive() {
        return active;
    }

    public LoginUserDbo getImpersonator() {
        return impersonator;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        id = rs.getLong("id");
        email = rs.getString("email");
        pwd = rs.getString("pwd");
        salt = rs.getString("salt");
        fname = rs.getString("fname");
        securityLevel = SecurityLevel.valueOf(rs.getInt("security_level"));
        superuser = rs.getBoolean("is_superuser");
        active = rs.getBoolean("is_active");
        timezone = TimeZone.getTimeZone(rs.getString("timezone"));
        locale = I18nHelper.getLocale(rs.getString("locale"));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    /**
     * Limited view of the object
     * NOTE: We omit password and salt in non-admin object
     * @return JsonObject
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = super.toJsonObject();
        obj.addProperty("id", id);
        if (email != null) obj.addProperty("email", email);
        if (fname != null) obj.addProperty("fname", email);
        if (locale != null) obj.addProperty("locale", locale.toString());
        if (timezone != null) obj.addProperty("timezone", timezone.getID());
        return obj;
    }

    /**
     * Password and internal status only admin visible
     * @return JsonObject
     */
    @Override
    public JsonObject toJsonObjectAdmin() {
        JsonObject obj = toJsonObject();
        if (pwd !=null) obj.addProperty("pwd", pwd);
        if (salt !=null) obj.addProperty("salt", pwd);
        if (locale != null) obj.addProperty("locale", locale.toString());
        if (timezone != null) obj.addProperty("timezone", timezone.getID());
        obj.addProperty("is_su", superuser);
        obj.addProperty("is_active", active);
        if (securityLevel != null) obj.addProperty("security_level", securityLevel.toString());
        return obj;
    }

    /**
     * Save last_login_on timestamp to now
     */
    public void touch() {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/Login/UpdateLastLoginOn.sql",
                        p -> p.setLong(1, id)
                )
        ) {
            pstmt.executeUpdate();
        } catch (Exception sqle) {
            LOGGER.error("Failed to update last_login_on timestamp", sqle);
        }
    }

    public void setImpersonator(LoginUserDbo impersonator) {
        this.impersonator = impersonator;
    }

//    /**
//     * Salt and digest the password then save it
//     *
//     * @param password String clear text password
//     */
//    public void savePassword(String password) {
//        if (StringUtils.isBlank(password))
//            throw new InvalidParameterException("Password cannot be blank");
//
//        String saltedPassword = SecurityHelper.encodeSaltPassword(password, email);
//        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
//        try (
//                Connection connection = dbm.getConnection();
//                PreparedStatement pstmt = dbm.prepareStatement(
//                        connection,
//                        "/sql/Login/UpdatePasswordById.sql",
//                        p -> {
//                            p.setString(1, saltedPassword);
//                            p.setLong(2, id);
//                        }
//                )
//        ) {
//            int rowsAffected = pstmt.executeUpdate();
//            if (1 != rowsAffected) {
//                LOGGER.warn("Unable to update password [{}] for login_id={}", saltedPassword, id);
//            }
//        } catch (Exception sqle) {
//            LOGGER.error("Failed to find login", sqle);
//        }
//    }


    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @SuppressWarnings("unused")
    private boolean setSuperuser() { throw new RuntimeException("Superuser flag cannot be externally set"); }
}
