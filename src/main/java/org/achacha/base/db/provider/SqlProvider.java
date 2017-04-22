package org.achacha.base.db.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Cached SQL string resource provider
 * Loads from resources SQL
 */
public abstract class SqlProvider {
    protected static final Logger LOGGER = LogManager.getLogger(SqlProvider.class);

    /**
     * Get SQL from resource path
     * @param resourcePath String
     * @return SQL string
     */
    public abstract String get(String resourcePath);
}
