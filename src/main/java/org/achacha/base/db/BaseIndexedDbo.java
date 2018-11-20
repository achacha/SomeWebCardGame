package org.achacha.base.db;

import org.apache.commons.lang3.NotImplementedException;

import java.sql.Connection;

/**
 * DataBase Object based by database table
 * Expected to have an id and can be cached
 *
 * insert and update methods need to be implemented by the child class if save is to be used
 **/
public abstract class BaseIndexedDbo extends BaseDbo {
    /**
     * @return Database object id
     */
    public abstract long getId();

    /**
     * Create a new instance and set id
     *
     * @param connection Connection to reuse
     * @throws Exception if unable to insert
     */
    public void insert(Connection connection) throws Exception {
        throw new NotImplementedException("Not implemented: "+getClass()+".insert()");
    }

    /**
     * Update current instance
     *
     * @param connection Connection to reuse
     * @throws Exception if unable to update
     */
    public void update(Connection connection) throws Exception {
        throw new NotImplementedException("Not implemented: "+getClass()+".update()");
    }
}
