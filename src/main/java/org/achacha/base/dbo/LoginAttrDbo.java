package org.achacha.base.dbo;

import com.google.common.base.Preconditions;
import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.global.Global;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Attribute associated with LoginPersona
 */
public class LoginAttrDbo extends BaseIndexedDbo {
    protected static final Logger LOGGER = LoggerFactory.getLogger(LoginAttrDbo.class);

    private long id;
    private long loginId;
    private String name;
    private String value;

    public long getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setLoginId(long loginId) {
        this.loginId = loginId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        id = rs.getLong("id");
        loginId = rs.getLong("login_id");
        name = rs.getString("name");
        value = rs.getString("value");
    }

    @Override
    public String toString() {
        return name+"="+value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(loginId).append(name).build();
    }

    @Override
    public void insert() throws Exception {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(id==0, "Object id must be 0 for insert");
        Preconditions.checkArgument(loginId > 0, "Must have a valid Login to create new attribute");

        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/LoginAttr/Insert.sql",
                        p -> {
                            p.setLong(1, loginId);
                            p.setString(2, name);
                            p.setString(3, value);
                        }
                )
        ) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
                LOGGER.debug("Inserted new LoginAttrDbo={}", this);
            }
            else {
                LOGGER.error("Failed to insert LoginAttrDbo="+this);
            }
        } catch (IOException sqle) {
            LOGGER.error("Failed to update login_attr, id={}", id, sqle);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoginAttrDbo) {
            LoginAttrDbo that = (LoginAttrDbo) obj;
            return new EqualsBuilder().append(this.loginId, that.loginId).append(this.name, that.name).append(this.value, that.value).build();
        }
        else
            return false;
    }

    @Override
    public void update() throws Exception {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(id>0, "Object id must be >0 for update");
        Preconditions.checkArgument(loginId>0, "Must have a valid Login to create new attribute");

        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/LoginAttr/UpdateNameValueById.sql",
                        p -> {
                            p.setString(1, name);
                            p.setString(2, value);
                            p.setLong(3, id);
                        }
                )
        ) {
            if (pstmt.executeUpdate() != 1) {
                LOGGER.warn("Unable to update login_attr, id={}", id);
            }
        } catch (IOException sqle) {
            LOGGER.error("Failed to update login_attr, id={}", id, sqle);
        }
    }
}
