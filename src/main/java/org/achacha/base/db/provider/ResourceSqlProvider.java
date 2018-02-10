package org.achacha.base.db.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.achacha.base.context.CallContext;
import org.achacha.base.global.Global;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

/**
 * Provide SQL from resource files
 */
public class ResourceSqlProvider implements SqlProvider {
    protected Logger LOGGER = LogManager.getLogger(SqlProvider.class);

    private final Cache<String, String> cachePathToSql;

    public ResourceSqlProvider() {
        cachePathToSql = CacheBuilder
                .newBuilder()
                .build();
    }

    /**
     * Load one SQL resource at given path
     * @param key String
     * @return String SQL
     * @throws IOException
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
     */
    public String get(String resourcePath) {
        try {
            String sql = cachePathToSql.get(resourcePath, () -> loadResource(resourcePath));
            if (sql == null)
                throw new RuntimeException("Resource not found: " + resourcePath);

            LOGGER.debug("Loading resource SQL at path='{}' with sql={}", resourcePath, sql);
            return sql;
        }
        catch (ExecutionException ee) {
            LOGGER.warn("Failed to execute cache loader for: "+resourcePath+", "+ee.getMessage());
            throw new RuntimeException("Failed to execute cache loader for: "+resourcePath, ee);
        }
    }
}
