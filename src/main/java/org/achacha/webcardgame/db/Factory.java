package org.achacha.webcardgame.db;

import org.achacha.webcardgame.dbo.Login;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class Factory {
    private static final Log LOGGER = LogFactory.getLog(Factory.class);

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
