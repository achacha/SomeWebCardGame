package org.achacha.webcardgame.web;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")  //@see MyServletContainer
public class MyApplication extends ResourceConfig {
    public MyApplication() {
        super(MyApplication.class);

        register(RolesAllowedDynamicFeature.class);
        register(EncodingFilter.class);
        register(GZipEncoder.class);
        register(DeflateEncoder.class);
        register(new MyBinder());
        packages(true, "org.achacha.webcardgame.web");

        LoggerFactory.getLogger(MyApplication.class).info("Application initialized");
    }

    private static class MyBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(MyApplication.class).to(MyApplication.class);
        }
    }
}
