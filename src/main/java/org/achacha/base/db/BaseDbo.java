package org.achacha.base.db;

import com.google.gson.JsonObject;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonEmittable;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DataBase Object
 *
 * Default constructor is required in derived classes
 * @see BaseDboFactory::createFromResultSet(ResultSet)
 *
 * Can be populated form ResultSet
 * @see BaseDboFactory::fromResultSet(ResultSet)
 *
 * Able to parse from JsonObject
 * @see JsonEmittable
 *
 * Expected to have an id and can be cached
 * @see #getId()
 *
 * insert and update methods need to be implemented by the child class if save is to be used
 * @see #insert(Connection)
 * @see #update(Connection)
 **/
public abstract class BaseDbo implements JsonEmittable {
    transient private static final Logger LOGGER = LogManager.getLogger(BaseDbo.class);

    /**
     * Create Dbo
     */
    public BaseDbo() {
    }

    /**
     * @return Database object id
     */
    public abstract long getId();

    /**
     * Create a new instance and set id
     *
     * @param connection Connection to reuse
     * @throws SQLException if unable to insert
     */
    public void insert(Connection connection) throws SQLException {
        throw new NotImplementedException("Not implemented: "+getClass()+".insert()");
    }

    /**
     * Update current instance
     *
     * @param connection Connection to reuse
     * @throws SQLException if unable to update
     */
    public void update(Connection connection) throws SQLException {
        throw new NotImplementedException("Not implemented: "+getClass()+".update()");
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
     * @throws SQLException if fails to get data from ResultSet
     */
    public abstract void fromResultSet(ResultSet rs) throws SQLException;
}
