package org.achacha.base.web;

import org.achacha.base.global.Global;
import org.achacha.base.global.GlobalForRoot;
import org.achacha.base.global.GlobalForTest;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServerGlobalInit implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String testMode = System.getProperty("TEST_MODE");
        boolean isEmbeddedTestServer = StringUtils.compare(testMode, "1") == 0;

        Global.setInstance(isEmbeddedTestServer ? new GlobalForTest(): new GlobalForRoot());
        Global.getInstance().init(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Global.shutdown();
    }
}
