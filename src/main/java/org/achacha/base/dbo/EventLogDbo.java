package org.achacha.base.dbo;

import com.google.gson.JsonObject;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.logging.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EventLogDbo extends BaseDbo {
    protected static final Logger LOGGER = LogManager.getLogger(EventLogDbo.class);

    protected long id;
    protected Timestamp createdOn;
    protected int eventId;
    protected int loginId;
    protected String data;

    public long getId() {
        return id;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public int getEventId() {
        return eventId;
    }

    public int getLoginId() {
        return loginId;
    }

    public String getData() {
        return data;
    }

    EventLogDbo() {
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        id = rs.getLong("id");
        createdOn = rs.getTimestamp("created_on");
        eventId = rs.getInt("event_id");
        loginId = rs.getInt("login_id");
        data = rs.getString("data");
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();

        obj.addProperty("id", id);
        obj.addProperty("created_on", createdOn.toString());
        obj.addProperty("event_id", eventId);
        obj.addProperty("login_id", loginId);

        obj.addProperty("data", data);

        return obj;
    }

    /**
     * @return Internal event name
     */
    public String getEventName() {
        return Event.valueOf(eventId).getResourceKey();
    }
}
