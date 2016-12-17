package org.achacha.webcardgame.web;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class MyApplication extends ResourceConfig {
    public MyApplication() {
        super(MyApplication.class);
        register(RolesAllowedDynamicFeature.class);
    }
}
