package org.achacha.base.dbo;

import com.google.gson.JsonObject;
import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.base.db.DatabaseManager;
import org.achacha.base.db.JdbcSession;
import org.achacha.base.global.Global;
import org.achacha.base.logging.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventLogDboFactory {
    /**
     * Insert event for the currently logged in user
     *
     * @param event EventLogDbo.Event
     */
    public static void insertFromContex(Event event) {
        CallContext context = CallContextTls.get();
        if (null != context.getLogin()) {

            DatabaseManager dbm = Global.getInstance().getDatabaseManager();
            try (
                    Connection connection = dbm.getConnection();
                    PreparedStatement pstmt = dbm.prepareStatement(
                            connection,
                            "/sql/EventLog/InsertNoData.sql",
                            p -> {
                                p.setLong(1, context.getLogin().getId());
                                p.setLong(2, event.getId());
                            }
                    )
            ) {
                pstmt.executeUpdate();
            } catch (Exception sqle) {
                EventLogDbo.LOGGER.error("Failed to insert event without data", sqle);
            }
        } else {
            EventLogDbo.LOGGER.error("Failed to log event for login, context does not have a valid logged in user, possible invalid use of event logging");
        }
    }

    /**
     * Insert event for the currently logged in user with JSON data
     *
     * @param event EventLogDbo.Event
     * @param data  JSON data
     */
    public static void insertFromContex(Event event, JsonObject data) {
        CallContext context = CallContextTls.get();
        if (null != context.getLogin()) {

            DatabaseManager dbm = Global.getInstance().getDatabaseManager();
            try (
                    Connection connection = dbm.getConnection();
                    PreparedStatement pstmt = dbm.prepareStatement(
                            connection,
                            "/sql/EventLog/InsertWithData.sql",
                            p -> {
                                p.setLong(1, context.getLogin().getId());
                                p.setLong(2, event.getId());
                                p.setString(3, data.toString());
                            }
                    )
            ) {
                pstmt.executeUpdate();
            } catch (Exception sqle) {
                EventLogDbo.LOGGER.error("Failed to insert event with data", sqle);
            }
        } else {
            EventLogDbo.LOGGER.error("Failed to log event for login, context does not have a valid logged in user, possible invalid use of event logging");
        }
    }

    /**
     * Insert internal event as server user with JSON data
     *
     * @param event EventLogDbo.Event
     * @param data  JSON data
     */
    public static void insertInternal(Event event, JsonObject data) {
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                Connection connection = dbm.getConnection();
                PreparedStatement pstmt = dbm.prepareStatement(
                        connection,
                        "/sql/EventLog/InsertInternalWithData.sql",
                        p -> {
                            p.setInt(1, event.getId());
                            p.setString(2, data.toString());
                        }
                )
        ) {
            pstmt.executeUpdate();
        } catch (Exception sqle) {
            EventLogDbo.LOGGER.error("Failed to insert internal event with data", sqle);
        }
    }

    /**
     * Get last 250 events within 1 day
     *
     * @return List of last 250 events in 1 day period
     */
    public static Collection<EventLogDbo> loadLast250Today() {
        List<EventLogDbo> events = new ArrayList<>();
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                JdbcSession triple = dbm.executeSql("/sql/EventLog/SelectTodayLast250.sql")
        ) {
            while (triple.getResultSet().next()) {
                EventLogDbo dbo = new EventLogDbo();
                dbo.fromResultSet(triple.getResultSet());
                events.add(dbo);
            }
        } catch (Exception sqle) {
            EventLogDbo.LOGGER.error("Failed to insert event without data", sqle);
        }
        return events;
    }

    /**
     * Get last 250 events
     *
     * @return List of last 250 events
     */
    public static Collection<EventLogDbo> loadLast250() {
        List<EventLogDbo> events = new ArrayList<>();
        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        try (
                JdbcSession triple = dbm.executeSql("/sql/EventLog/SelectAnyLast250.sql")
        ) {
            while (triple.getResultSet().next()) {
                EventLogDbo dbo = new EventLogDbo();
                dbo.fromResultSet(triple.getResultSet());
                events.add(dbo);
            }
        } catch (Exception sqle) {
            EventLogDbo.LOGGER.error("Failed to insert event without data", sqle);
        }
        return events;
    }
}
