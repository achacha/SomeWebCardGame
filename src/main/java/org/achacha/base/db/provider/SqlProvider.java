package org.achacha.base.db.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cached SQL string resource provider
 * Loads from resources SQL
 */
public abstract class SqlProvider {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SqlProvider.class);

    /**
     * Get SQL from resource path
     * @param resourcePath String
     * @return SQL string
     */
    public abstract String get(String resourcePath);
}
