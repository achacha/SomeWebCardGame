package org.achacha.webcardgame.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.achacha.base.global.GlobalProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebConfig;

import java.io.IOException;

@WebServlet(
        name = "API app",
        urlPatterns = GlobalProperties.URI_API_BASE+"/*",
        loadOnStartup = 1,
        initParams = {
                @WebInitParam(
                        name="com.sun.jersey.spi.container.ContainerRequestFilters",
                        value="org.achacha.webcardgame.web.filter.AuthenticationFilter"
                ),
                @WebInitParam(
                        name = "jersey.config.server.provider.packages",
                        value = "org.achacha.webcardgame.web"
                ),
                @WebInitParam(
                        name = "jakarta.ws.rs.Application",
                        value = "org.achacha.webcardgame.web.MyApplication"
                )
        }
)
public class MyServletContainer extends ServletContainer {
    private static final Logger LOGGER = LogManager.getLogger(MyServletContainer.class);

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
