package org.achacha.base.cache;

import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.db.DboHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Dbo objects that are cached
 */
public class DboCacheManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DboCacheManager.class);

    private static final DboCacheManager instance = new DboCacheManager();
    public static DboCacheManager getInstance() {
        return instance;
    }

    /** Data store by class */
    private Map<Class<? extends BaseIndexedDbo>, DboCache> caches = new HashMap<>();

    /** Map simple name to Class for cached clases */
    private Map<String, Class<? extends BaseIndexedDbo>> simpleNameToClass = new HashMap<>();

    /**
     * Initialize caches
     */
    public void init() {
        // Get all classes annotated with @CachedDbo and create a cache for each
        Set<Class<? extends BaseIndexedDbo>> classes = DboHelper.getAllCachedDboClasses();
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
     * @param clazz Class E of the BaseIndexedDbo
     * @param id long id of the object or pass <=0 to guarantee null
     * @param <E> type of BaseIndexedDbo
     * @return E or null if not found
     * @see #init()
     */
    public <E extends BaseIndexedDbo> E getById(Class<E> clazz, long id) {
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
     * @param <E> extends BaseIndexedDbo
     * @return
     */
    public <E extends BaseIndexedDbo> DboCache<E> getCache(Class<E> clazz) {
        DboCache<E> cache = caches.get(clazz);
        if (cache == null)
            throw new IllegalArgumentException("Cache for class="+clazz.getName()+" does not exist");

        return cache;
    }

    /**
     * @return Key set of caches
     */
    public Set<Class<? extends BaseIndexedDbo>> keySet() {
        return caches.keySet();
    }

    /**
     * @return Class from simple name
     */
    public Class<? extends BaseIndexedDbo> forName(String simpleName) {
        return simpleNameToClass.get(simpleName);
    }
}
