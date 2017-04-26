package org.achacha.webcardgame.web.filter;

import org.achacha.base.context.CallContext;
import org.achacha.base.context.CallContextTls;
import org.achacha.webcardgame.helper.LoginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "RootAuthFilter", value = "/*")
public class RootFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootFilter.class);

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;

        boolean contextSet = false;
        long startTime = System.nanoTime();
        try {
            // Check if this is a request for login, if not make sure security is enforced
            String uri = CallContext.getUriRelativeToWebContext(request);
            LOGGER.debug("Processing relative URI: {}", uri);

            // Create CallContext for non-static calls
            if (!LoginHelper.isStaticUri(uri)) {
                LOGGER.debug("Creating CallContext for {}", uri);
                // Put CallContext into TLS
                CallContext context = new CallContext(request, (HttpServletResponse) resp, request.getMethod());
                CallContextTls.set(context);
                contextSet = true;
            }
            else {
                LOGGER.debug("Skipping CallContext for {}", uri);
            }

            // Continue on to other filter chains, authetication will be done by the auth filter further down
            chain.doFilter(req, resp);
        }
        finally {
            HttpServletResponse response = (HttpServletResponse)resp;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Finished processing URI={}  status={}  t={}us", request.getRequestURI(), response.getStatus(), (System.nanoTime() - startTime)/1000);
            }

            // Context was set on Tls in this scope, clean it up for consistency
            if (contextSet) {
                CallContextTls.unset();
            }
        }
    }

    public void init(FilterConfig config) throws ServletException {}
    public void destroy() {}
}
