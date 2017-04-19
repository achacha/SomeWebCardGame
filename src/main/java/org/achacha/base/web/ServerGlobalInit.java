package org.achacha.base.web;

import org.achacha.base.global.Global;
import org.achacha.base.global.RootGlobal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServerGlobalInit implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
            Global.setInstance(new RootGlobal());
            Global.getInstance().init(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Global.shutdown();
    }
}
