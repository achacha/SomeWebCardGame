package org.achacha.webcardgame.web;

import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.junit.jupiter.api.Test;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AbstractRoutesTest {
    @Test
    void verifyAllPathedMethodsHaveSecurityLevel() {
        Configuration config = ConfigurationBuilder.build()
                .forPackages(AbstractRoutes.class.getPackageName())
                .addUrls(ClasspathHelper.forPackage(AbstractRoutes.class.getPackageName()))
                .addScanners(new MethodAnnotationsScanner())
                .filterInputsBy(
                        input -> input != null && input.endsWith(".class") && input.startsWith("org/achacha") && !input.contains("/test/")
                );

        Reflections reflections = new Reflections(config);
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Path.class);
        methods.forEach(method->{
            SecurityLevelRequired slr = method.getAnnotation(SecurityLevelRequired.class);
            assertNotNull(slr, "API method missing @SecurityLevelRequired: "+method.getName());

        });
    }

}