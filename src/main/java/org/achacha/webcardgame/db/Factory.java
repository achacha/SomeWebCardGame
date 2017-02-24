package org.achacha.webcardgame.db;

import org.achacha.webcardgame.dbo.Login;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Factory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);

    private static final Factory instance = new Factory();

    private SessionFactory sessionFactory;

    public static Factory getInstance() {
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void init() {
        if (sessionFactory == null) {
            final Configuration configuration = new Configuration()
                    .addAnnotatedClass(Login.class)
                    .addPackage("org.achacha.webcardgame.dbo")
                    .configure();

            final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            try {
                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception e) {
                LOGGER.error("Database session factory initialized");

                // The registry would be destroyed by the SessionFactory,
                // but we had trouble building the SessionFactory
                // so destroy it manually.
                StandardServiceRegistryBuilder.destroy(registry);

                throw e;
            }

            LOGGER.info("Database session factory initialized");
        }
    }

    public void destroy() {
        sessionFactory.close();
    }

}
