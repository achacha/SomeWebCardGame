package org.achacha.webcardgame.web;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                ),
                @WebInitParam(
                        name = "javax.ws.rs.Application",
                        value = "org.achacha.webcardgame.web.MyApplication"
                )

        }
)
public class MyServletContainer extends ServletContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyServletContainer.class);

    @Override
    protected void init(WebConfig webConfig) throws ServletException {
        LOGGER.info("MyServletContainer.init: START");
        super.init(webConfig);
        LOGGER.info("MyServletContainer.init: END");
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }
}
