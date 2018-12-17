package org.achacha.base.dbo;

import com.google.gson.JsonObject;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Table(schema="public", name="login")
public class LoginPersonaDbo extends LoginUserDbo {

    protected String lname;
    protected String address1;
    protected String address2;
    protected String city;
    protected String state;
    protected String postal;
    protected String country;
    protected String phone1;
    protected String phone2;

    protected Collection<LoginAttrDbo> attrs;

    public LoginPersonaDbo() {
    }

    @Override
    public void fromResultSet(Connection connection, ResultSet rs) throws SQLException {
        super.fromResultSet(connection, rs);

        lname = rs.getString("lname");
        address1 = rs.getString("address1");
        address2 = rs.getString("address2");
        city = rs.getString("city");
        state = rs.getString("state");
        country = rs.getString("country");
        postal = rs.getString("postal");
        phone1 = rs.getString("phone1");
        phone2 = rs.getString("phone2");
        attrs = Global.getInstance().getDatabaseManager().<LoginAttrDboFactory>getFactory(LoginAttrDbo.class).findByLoginId(connection, id);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("fromResultSet: this="+this);
        }
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = super.toJsonObject();

        if (lname != null) obj.addProperty("lname", lname);
        if (address1 != null) obj.addProperty("address1", address1);
        if (address2 != null) obj.addProperty("address2", address2);
        if (city != null) obj.addProperty("city", city);
        if (state != null) obj.addProperty("state", state);
        if (country != null) obj.addProperty("country", country);
        if (postal != null) obj.addProperty("postal", postal);
        if (phone1 != null) obj.addProperty("phone1", phone1);
        if (phone2 != null) obj.addProperty("phone2", phone2);

        // Lazy load and add attributes
        obj.add("attrs", JsonHelper.toJsonArray(attrs));

        return obj;
    }

    /**
     * Login attributes associated with this object
     * @return Collection of LoginAttrDbo
     */
    public Collection<LoginAttrDbo> getAttrs() {
        return attrs;
    }
}
