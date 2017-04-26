package org.achacha.webcardgame.helper;

import org.achacha.base.json.JsonHelper;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

public class ResponseHelper {
    /**
     * Response 401 unauthorized
     * @param message String to display inside JSON response
     * @return Response built and ready to return
     */
    public static Response getAuthFailed(String message) {
        return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(JsonHelper.getFailObject(message)).build();
    }
}
