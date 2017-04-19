package org.achacha.base.db;

import org.achacha.base.cache.CachedDbo;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public class DboHelper {
    /**
     * @return Set of Class of type BaseDbo
     */
    public static Set<Class<? extends BaseDbo>> getAllDboClasses() {
        Reflections reflections = new Reflections("org.achacha.base.dbo");

        Set<Class<? extends BaseDbo>> classes = new HashSet<>();
        classes.addAll(reflections.getSubTypesOf(BaseDbo.class));
        classes.addAll(reflections.getSubTypesOf(BaseIndexedDbo.class));
        return classes;
    }

    /**
     * @return Set of Class of type BaseDbo with CachedDbo annotation
     */
    public static Set<Class<? extends BaseIndexedDbo>> getAllCachedDboClasses() {
        Reflections reflections = new Reflections("org.achacha.base.dbo");

        final HashSet<Class<? extends BaseIndexedDbo>> set = new HashSet<>();
        reflections.getTypesAnnotatedWith(CachedDbo.class).forEach(c -> {
            set.add((Class<? extends BaseIndexedDbo>) c);
        });
        return set;
    }
}
