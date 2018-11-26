package org.achacha.base.cache;

import org.achacha.base.db.BaseDbo;
import org.achacha.base.db.DboClassHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Dbo objects that are cached
 */
public class DboCacheManager {
    private static final Logger LOGGER = LogManager.getLogger(DboCacheManager.class);

    private static final DboCacheManager instance = new DboCacheManager();
    public static DboCacheManager getInstance() {
        return instance;
    }

    /** Data store by class */
    private Map<Class<? extends BaseDbo>, DboCache> caches = new HashMap<>();

    /** Map simple name to Class for cached clases */
    private Map<String, Class<? extends BaseDbo>> simpleNameToClass = new HashMap<>();

    /**
     * Initialize caches
     */
    public void init() {
        // Get all classes annotated with @CachedDbo and create a cache for each
        Set<Class<? extends BaseDbo>> classes = DboClassHelper.getAllCachedDboClasses();
        classes.stream()
                .sorted((a,b)->{
                    // Use annotation order() to sort lowest to highest
                    CachedDbo aa = a.getAnnotation(CachedDbo.class);
                    CachedDbo ab = b.getAnnotation(CachedDbo.class);
                    return aa.order() - ab.order();
                })
                .forEach(clazz -> {
                    CachedDbo cdbo = clazz.getAnnotation(CachedDbo.class);
                    caches.put(clazz, new DboCache<>(clazz, cdbo.selectAll(), cdbo.selectById(), cdbo.preload()));
                    simpleNameToClass.put(clazz.getSimpleName(), clazz);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Loaded cache for: cdbo={} class={} CachedDbo={} size={}", cdbo, clazz.getSimpleName(), cdbo.toString(), caches.get(clazz).size());
                    }
                });

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("DatabaseCacheManager initialized={}", caches.keySet());
        }
    }

    /**
     * Get Dbo type by id from an already loaded cache
     * Can be used directly when populating from database since id of 0 will always return null
     *
     * @param clazz Class E of the BaseDbo
     * @param id long id of the object or pass &lt;=0 to guarantee null
     * @param <E> type of BaseDbo
     * @return E or null if not found
     * @see #init()
     */
    public <E extends BaseDbo> E getById(Class<E> clazz, long id) {
        DboCache<E> cache = caches.get(clazz);
        if (cache == null)
            throw new IllegalArgumentException("Cache for class="+clazz.getName()+" does not exist");

        if (id > 0)
            return cache.getById(id);
        else {
            return null;
        }
    }

    /**
     * Get DboCache object associated with the Class
     * @param clazz Class of E
     * @param <E> extends BaseDbo
     * @return DboCache of E
     */
    public <E extends BaseDbo> DboCache<E> getCache(Class<E> clazz) {
        DboCache<E> cache = caches.get(clazz);
        if (cache == null)
            throw new IllegalArgumentException("Cache for class="+clazz.getName()+" does not exist");

        return cache;
    }

    /**
     * @return Key set of caches
     */
    public Set<Class<? extends BaseDbo>> keySet() {
        return caches.keySet();
    }

    /**
     * @param simpleName String to lookup against a class name
     * @return Class from simple name
     */
    public Class<? extends BaseDbo> forName(String simpleName) {
        return simpleNameToClass.get(simpleName);
    }
}
