package org.achacha.base.db;

import com.google.common.base.Preconditions;
import org.achacha.base.cache.CachedDbo;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.Nonnull;
import javax.persistence.Table;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class DboHelper {
    /**
     * Packages where DBOs exist
     */
    private static final String[] DBO_LOCATIONS = {
            "org.achacha.base.dbo",
            "org.achacha.webcardgame.game.dbo"
    };

    /**
     * @return Reflections configuration for scanning Dbo classes
     */
    private static Configuration getConfiguration() {
        return ConfigurationBuilder.build()
                .forPackages(DBO_LOCATIONS)
                .addUrls(ClasspathHelper.forJavaClassPath())
                .addScanners()
                .filterInputsBy(
                        input -> input != null && input.endsWith(".class") && input.startsWith("org/achacha") && !input.contains("/test/")
                );
    }

    /**
     * @return Set of Class of type BaseDbo
     */
    public static Set<Class<? extends BaseDbo>> getAllDboClasses() {
        Reflections reflections = new Reflections(getConfiguration());

        Set<Class<? extends BaseDbo>> classes = new HashSet<>(reflections.getSubTypesOf(BaseDbo.class));
        classes.removeIf(clz -> Modifier.isAbstract(clz.getModifiers()));  // Remove abstract classes
        return classes;
    }

    /**
     * @return Set of Class of type BaseIndexedDbo
     */
    public static Set<Class<? extends BaseIndexedDbo>> getIndexedDboClasses() {
        Reflections reflections = new Reflections(getConfiguration());

        Set<Class<? extends BaseIndexedDbo>> classes = new HashSet<>(reflections.getSubTypesOf(BaseIndexedDbo.class));
        classes.removeIf(clz -> Modifier.isAbstract(clz.getModifiers()));  // Remove abstract classes
        return classes;
    }

    /**
     * @return Set of Class of type BaseDbo with CachedDbo annotation
     */
    public static Set<Class<? extends BaseIndexedDbo>> getAllCachedDboClasses() {
        Reflections reflections = new Reflections(getConfiguration());

        final HashSet<Class<? extends BaseIndexedDbo>> set = new HashSet<>();
        reflections.getTypesAnnotatedWith(CachedDbo.class).forEach(c -> set.add((Class<? extends BaseIndexedDbo>) c));
        return set;
    }

    /**
     * @return Set of Class of type BaseDboFactory
     */
    public static Set<Class<? extends BaseDboFactory>> getAllDboFactories() {
        Reflections reflections = new Reflections(getConfiguration());

        Set<Class<? extends BaseDboFactory>> classes = new HashSet<>(reflections.getSubTypesOf(BaseDboFactory.class));
        classes.removeIf(clz -> Modifier.isAbstract(clz.getModifiers()));  // Remove abstract classes
        return classes;
    }

    /**
     * Get table from @Table on Dbo
     * Will throw exception if annotation missing
     * @param dboClass Dbo class
     * @param <T> extends BaseIndexedDbo
     * @return Table
     */
    @Nonnull
    public static <T extends BaseIndexedDbo> Table getTableAnnotation(Class<T> dboClass) {
        Table[] tables = dboClass.getDeclaredAnnotationsByType(Table.class);
        Preconditions.checkState(Preconditions.checkNotNull(tables).length > 0);
        return tables[0];
    }

    @Nonnull
    public static <T extends BaseIndexedDbo> String getTable(Class<T> dboClass) {
        Table table = getTableAnnotation(dboClass);
        return table.schema()+"."+table.name();
    }
}
