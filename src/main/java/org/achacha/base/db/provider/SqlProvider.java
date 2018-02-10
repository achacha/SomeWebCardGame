package org.achacha.base.db.provider;

/**
 * Cached SQL string resource provider
 * Loads from resources SQL
 */
public interface SqlProvider {
    /**
     * Get SQL from resource path
     * @param resourcePath String
     * @return SQL string
     */
    String get(String resourcePath);
}
