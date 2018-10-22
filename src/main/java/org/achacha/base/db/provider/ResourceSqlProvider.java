package org.achacha.base.db.provider;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.achacha.base.context.CallContext;
import org.achacha.base.global.Global;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provide SQL from resource files
 */
public class ResourceSqlProvider extends SqlProvider {
    protected Logger LOGGER = LogManager.getLogger(SqlProvider.class);

    private final Cache<String, String> cachePathToSql;

    public ResourceSqlProvider() {
        cachePathToSql = Caffeine
                .newBuilder()
                .build();
    }

    /**
     * Load one SQL resource at given path
     * @param key String
     * @return String SQL
     * @throws IOException if cannot load resource
     */
    @Nonnull
    private static String loadResource(@Nonnull String key) throws IOException {
        InputStream is = Global.getInstance().getClass().getResourceAsStream(key);
        if (is == null)
            throw new FileNotFoundException("Unable to locate resource: "+key);

        return IOUtils.toString(is, CallContext.DEFAULT_ENCODING);
    }

    /**
     * Get SQL from resource path
     * @param resourcePath String
     * @return SQL string
     * @exception RuntimeException when not found or unable to load
     */
    @Nonnull
    @Override
    public String get(String resourcePath) {
        String sql = cachePathToSql.get(resourcePath, (key) -> {
            try {
                return loadResource(resourcePath);
            } catch (IOException e) {
                // Exception thrown when this returns null
                LOGGER.debug("Failed to load resource="+key, e);
                return null;
            }
        });
        if (sql == null)
            throw new RuntimeException("Resource not found: " + resourcePath);

        LOGGER.debug("Loading resource SQL at path='{}' with sql={}", resourcePath, sql);
        return sql;
    }
}
