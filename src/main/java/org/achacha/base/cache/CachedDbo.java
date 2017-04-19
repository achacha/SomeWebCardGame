package org.achacha.base.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated Dbo classes will be loaded into DboCacheManager automatically
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedDbo {
    /**
     * @return path that points to SQL for selecting all rows for caching
     */
    String selectAll();

    /**
     * @return path that points to SQL for selecting one row by id
     */
    String selectById();

    /**
     * @return If true it will load all data on startup, otherwise the cache will load row on demand
     */
    boolean preload() default false;

    /**
     * This is to allow some caches to initialize after other classes
     * Assign a higher value to delay initialization until lower order caches are initialized
     * @return Order number, hugher the number the later it will be initialized
     */
    int order() default 0;
}
