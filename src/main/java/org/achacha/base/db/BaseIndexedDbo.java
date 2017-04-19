package org.achacha.base.db;

import org.apache.commons.lang3.NotImplementedException;

/**
 * DataBase Object based by database table
 * Expected to have an id and can be cached
 **/
public abstract class BaseIndexedDbo extends BaseDbo {
    /**
     * @return Database object id
     */
    public abstract long getId();

    /**
     * Save the current object
     * if (id > 0) it calls update otherwise insert
     *
     * @throws Exception if unable to save
     */
    public void save() throws Exception {
        if (getId() == 0)
            insert();
        else
            update();
    }

    /**
     * Create a new instance and set id
     * @throws Exception
     */
    protected void insert() throws Exception {
        throw new NotImplementedException("Not implemented: "+getClass()+".insert()");
    }

    /**
     * Update current instance
     * @throws Exception
     */
    protected void update() throws Exception {
        throw new NotImplementedException("Not implemented: "+getClass()+".update()");
    }
}
