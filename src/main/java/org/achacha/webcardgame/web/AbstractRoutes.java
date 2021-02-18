package org.achacha.webcardgame.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

public class AbstractRoutes {
    @Context
    protected HttpServletRequest httpRequest;

    @Context
    protected ContainerRequestContext requestContext;
}
