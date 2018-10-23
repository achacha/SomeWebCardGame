package org.achacha.base.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.gson.JsonObject;
import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.db.JdbcSession;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonEmittable;
import org.achacha.base.json.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for specific indexed Dbo
 * Will attempt to load and then cache any item not found
 * Optionally reloads all table data with ability to load new data
 */
public class DboCache<E extends BaseIndexedDbo> implements JsonEmittable {
    protected static final Logger LOGGER = LogManager.getLogger(DboCache.class);

    /**
     * Cache to contain all the data
     * Invalidate can clean one item and reload on demand
     */
    protected Cache<Long, E> data;

    /**
     * Set of IDs that do not have a DB value
     * Use invalidate to clear it if the data becomes available
     */
    protected Map<Long, Boolean> doesNotExist = new ConcurrentHashMap<>();

    /**
     * Path to sql file to select all
     */
    protected final String sqlSelectAll;

    /**
     * Path to sql file to select by id
     */
    protected final String sqlSelectById;

    /**
     * Class of the Dbo
     */
    protected final Class<E> dboClass;

    /**
     * If data was preloaded
     */
    private final boolean isPreloaded;

    /**
     * Construct cache and configure SQL paths for selecting data
     *
     * @param dboClass Class of E used to construct new Dbo objects
     * @param sqlSelectAll  String path to resource
     * @param sqlSelectById String path to resource
     * @param preloadData boolean if true will load all table data
     */
    public DboCache(Class<E> dboClass, String sqlSelectAll, String sqlSelectById, boolean preloadData) {
        this.dboClass = dboClass;
        this.sqlSelectAll = sqlSelectAll;
        this.sqlSelectById = sqlSelectById;
        this.isPreloaded = preloadData;
        this.data = Caffeine.newBuilder().build();

        if (preloadData) {
            // Load all data
            try (
                    JdbcSession tuple = Global.getInstance().getDatabaseManager().executeSql(
                            sqlSelectAll,
                            p -> {}
                    )
            ) {
                while (tuple.getResultSet().next()) {
                    //TODO: Fix deprecated
                    E dbo = dboClass.newInstance();
                    dbo.fromResultSet(tuple.getResultSet());
                    data.put(dbo.getId(), dbo);
                }
            } catch (Exception sqle) {
                LOGGER.error("Failed to load all for {} using &sql={}", dboClass.getSimpleName(), sqlSelectById, sqle);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Preloaded cache for {}, count={}", dboClass.getSimpleName(), data.estimatedSize());
            }
        }
    }

    /**
     * Get Dbo by Id
     * @param id long
     * @return Dbo of type E
     */
    @Nullable
    public E getById(long id) {
        // If this id was not found in DB before, just return null
        if (doesNotExist.containsKey(id)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Item doesNotExist, class={} id={}", dboClass.getSimpleName(), id);
            }
            return null;
        }

        return data.get(id, (key) -> {
            try (
                    JdbcSession tuple = Global.getInstance().getDatabaseManager().executeSql(
                            sqlSelectById,
                            p -> p.setLong(1, id)
                    )
            ) {
                if (tuple.getResultSet().next()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Loading item into cache from DB, class={} id={}", dboClass.getSimpleName(), id);
                    }
                    // TODO: Fix deprecated
                    E dbo = dboClass.newInstance();
                    dbo.fromResultSet(tuple.getResultSet());
                    return dbo;
                } else {
                    // This does not exist in DB, don't check again in the future
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Item not found in DB, class={} id={}", dboClass.getSimpleName(), id);
                    }
                    doesNotExist.put(id, Boolean.TRUE);
                    return null;
                }
            } catch (Exception sqle) {
                LOGGER.error("Failed to load {} with id={} using &sql={}", dboClass.getSimpleName(), id, sqlSelectById, sqle);
                return null;
            }
        });
    }

    /**
     * Invalidate both cache and doesNotExist set for the id provided
     * @param id long
     */
    public void invalidateById(long id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Invalidating class={} id={}", dboClass.getSimpleName(), id);
        }
        doesNotExist.remove(id);
        data.invalidate(id);
    }

    /**
     * @return Collection of all E
     */
    public Collection<E> values() {
        return data.asMap().values();
    }

    /**
     * @return size of cache
     */
    public long size() {
        return data.estimatedSize();
    }

    /**
     * @return true if data was preloaded
     */
    public boolean isPreloaded() {
        return isPreloaded;
    }

    @Override
    public JsonObject toJsonObjectAdmin() {
        JsonObject obj = toJsonObject();

        obj.addProperty("is_preloaded", isPreloaded);
        CacheStats stats = data.stats();
        obj.addProperty("hit_count", stats.hitCount());
        obj.addProperty("hit_rate", stats.hitRate());
        obj.addProperty("miss_count", stats.missCount());
        obj.addProperty("miss_rate", stats.missRate());
        obj.addProperty("request_count", stats.requestCount());
        obj.addProperty("load_count", stats.loadCount());
        obj.addProperty("total_load_time", stats.totalLoadTime());
        obj.addProperty("load_success_count", stats.loadSuccessCount());
        obj.addProperty("load_failure_count", stats.loadFailureCount());
        obj.addProperty("load_failure_rate", stats.loadFailureRate());

        obj.add(JsonHelper.DATA, JsonHelper.toJsonArray(values()));

        return obj;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("size", data.estimatedSize());
        return obj;
    }
}
