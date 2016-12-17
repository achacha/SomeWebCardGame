package org.achacha.webcardgame.web;

import org.achacha.webcardgame.db.Factory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name="API app",
        urlPatterns = "/api/*",
        loadOnStartup = 1,
        initParams = {
                @WebInitParam(
                        name = "jersey.config.server.provider.packages",
                        value = "org.achacha.webcardgame.web"
                )
        }
)
public class MyServletContainer extends ServletContainer {
    private static final Log LOGGER = LogFactory.getLog(MyServletContainer.class);

    @Override
    protected void init(WebConfig webConfig) throws ServletException {
        LOGGER.info("MyServletContainer.init: START");
        super.init(webConfig);
        Factory.getInstance().init();   // Hibernate
        LOGGER.info("MyServletContainer.init: END");
    }

    @Override
    public void destroy() {
        Factory.getInstance().destroy();
        super.destroy();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }
}
