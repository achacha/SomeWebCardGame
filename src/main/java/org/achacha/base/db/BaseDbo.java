package org.achacha.base.db;

import com.google.gson.JsonObject;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonEmittable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DataBase Object
 *
 * Default constructor is required in derived classes
 * Able to parse from JsonObject
 * Can be populated form ResultSet
 **/
public abstract class BaseDbo implements JsonEmittable {
    private static final Logger LOGGER = LogManager.getLogger(BaseDbo.class);

    /**
     * Create Dbo
     */
    public BaseDbo() {
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    /**
     * Parse JsonObject and build BaseDbo type from it
     * @param jobj JsonObject
     * @param clazz Class of T
     * @param <T> type of BaseDbo
     * @return Object of type BaseDbo populated with data from JsonObject
     */
    public static <T extends BaseDbo> T from(JsonObject jobj, Class<T> clazz) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsing Dbo Class<"+clazz.getName()+"> from JSON="+jobj.toString());
        }
        return Global.getInstance().getGson().fromJson(jobj, clazz);
    }

    /**
     * Populate data from ResultSet
     * @param rs ResultSet
     * @throws SQLException
     */
    public abstract void fromResultSet(ResultSet rs) throws SQLException;
}
