package org.achacha.base.db;

import org.achacha.base.global.Global;

/**
 * Base class for all DboFactory types
 * Contains an instance of the current DatabaseManager
 */
public class BaseDboFactory {
    /**
     * DatabaseManager
     * If this is null then maybe DboFactory is being used before DatabaseManager is initialized
     */
    protected static final DatabaseManager dbm = Global.getInstance().getDatabaseManager();
}
