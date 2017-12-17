package org.achacha.webcardgame.web;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;

public class AbstractRoutes {
    @Context
    protected HttpServletRequest httpRequest;

    @Context
    protected ContainerRequestContext requestContext;
}
