package org.achacha.base.db.provider;

import org.flywaydb.core.internal.util.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Cached SQL string resource provider
 * Loads from resources SQL
 */
public abstract class SqlProvider {
    /**
     * Get SQL from resource path
     * @param resourcePath String
     * @return SQL string
     */
    @Nonnull
    public abstract String get(String resourcePath);

    /**
     * Builder for the resource SQL that allows modification/processing before returning actual SQL
     * This is used for parameter substitution of the actual SQL before parametrization
     * @param resourcePath String
     * @return Builder
     */
    @Nonnull
    public Builder builder(String resourcePath) {
        return new Builder(resourcePath);
    }

    /**
     * Allow parameter setting inside SQL
     */
    public class Builder {
        // Resource path that will be used to call SqlProvider.get before processing
        private final String resourcePath;

        // Map of token to string to replace
        private Map<String,String> tokens = new HashMap<>();

        private Builder(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        public Builder withToken(String key, String value) {
            tokens.put(key, value);
            return this;
        }

        /**
         * Substitute tokens and return final string
         * @return String
         */
        public String build() {
            String sql = get(resourcePath);

            for(Map.Entry<String,String> entry : tokens.entrySet()) {
                sql = StringUtils.replaceAll(sql, "${"+entry.getKey()+"}", entry.getValue());
            }

            return sql;
        }
    }

}
