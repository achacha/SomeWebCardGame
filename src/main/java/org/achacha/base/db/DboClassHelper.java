package org.achacha.base.db;

import com.google.common.base.Preconditions;
import org.achacha.base.cache.CachedDbo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.annotation.Nonnull;
import javax.persistence.Table;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class DboClassHelper {
    private static final Logger LOGGER = LogManager.getLogger(DboClassHelper.class);

    /**
     * Packages where DBOs exist
     */
    private static final String[] DBO_LOCATIONS = {
            "org.achacha.base.dbo",
            "org.achacha.webcardgame.game.dbo"
    };

    private static final Reflections reflections = new Reflections(getConfiguration());

    /**
     * @return Reflections configuration for scanning Dbo classes
     */
    private static Configuration getConfiguration() {
        HashSet<URL> urls = new HashSet<>();
        for (String location : DBO_LOCATIONS) {
            urls.addAll(ClasspathHelper.forPackage(location));
        }

        return ConfigurationBuilder.build()
                .forPackages(DBO_LOCATIONS)
                .addUrls(urls)
                .addScanners()
                .filterInputsBy(
                        input -> input != null && input.endsWith(".class") && input.startsWith("org/achacha") && !input.contains("/test/")
                );
    }

    /**
     * @return Set of Class of type BaseDbo
     */
    public static Set<Class<? extends BaseDbo>> getAllDboClasses() {
        Set<Class<? extends BaseDbo>> classes = new HashSet<>(reflections.getSubTypesOf(BaseDbo.class));
        classes.removeIf(clz -> Modifier.isAbstract(clz.getModifiers()));  // Remove abstract classes
        return classes;
    }

    /**
     * @return Set of Class of type BaseDbo
     */
    public static Set<Class<? extends BaseDbo>> getIndexedDboClasses() {
        Set<Class<? extends BaseDbo>> classes = new HashSet<>(reflections.getSubTypesOf(BaseDbo.class));
        classes.removeIf(clz -> Modifier.isAbstract(clz.getModifiers()));  // Remove abstract classes
        return classes;
    }

    /**
     * @return Set of Class of type BaseDbo with CachedDbo annotation
     */
    public static Set<Class<? extends BaseDbo>> getAllCachedDboClasses() {
        final HashSet<Class<? extends BaseDbo>> set = new HashSet<>();
        reflections.getTypesAnnotatedWith(CachedDbo.class).forEach(c -> set.add((Class<? extends BaseDbo>) c));
        return set;
    }

    /**
     * @return Set of Class of type BaseDboFactory
     */
    public static Set<Class<? extends BaseDboFactory>> getAllDboFactories() {
        Set<Class<? extends BaseDboFactory>> classes = new HashSet<>(reflections.getSubTypesOf(BaseDboFactory.class));
        classes.removeIf(clz -> {
            boolean isAbstract = Modifier.isAbstract(clz.getModifiers());
            if (isAbstract)
                LOGGER.info("Ignoring abstract factory: {}", clz.getName());
            return isAbstract;
        } );  // Remove abstract classes
        return classes;
    }

    /**
     * Get table from @Table on Dbo
     * Will throw exception if annotation missing
     * @param dboClass Dbo class
     * @param <T> extends BaseDbo
     * @return Table
     */
    @Nonnull
    public static <T extends BaseDbo> Table getTableAnnotation(Class<T> dboClass) {
        Table[] tables = dboClass.getDeclaredAnnotationsByType(Table.class);
        Preconditions.checkState(Preconditions.checkNotNull(tables).length > 0);
        return tables[0];
    }

    @Nonnull
    public static <T extends BaseDbo> String getTable(Class<T> dboClass) {
        Table table = getTableAnnotation(dboClass);
        return table.schema()+"."+table.name();
    }
}
