package org.achacha.webcardgame.web.v1.admin;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.dbo.LoginUserDbo;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.web.AbstractRoutes;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * /api/admin/login
 */
@Path("admin/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginRoutes extends AbstractRoutes {
    private static final Logger LOGGER = LogManager.getLogger(LoginRoutes.class);

    @GET
    @Path("{id}")
    @SecurityLevelRequired(value = SecurityLevel.ADMIN, requiresSuperuser = true)
    public Response getFullUserById(@PathParam("id") long id) {
        Preconditions.checkState(id > 0);

        BaseDboFactory<LoginUserDbo> factory = Global.getInstance().getDatabaseManager().getFactory(LoginUserDbo.class);
        LoginUserDbo login = factory.getById(id);

        return Response.ok(login).build();
    }

    @PUT
    @SecurityLevelRequired(value = SecurityLevel.ADMIN, requiresSuperuser = true)
    public Response setPassword(JsonObject data) {
        Preconditions.checkNotNull(data);
        Preconditions.checkState(data.has("loginId"));
        Preconditions.checkState(data.has("pwd"));

        JsonObject jobj;
        long loginId = data.get("loginId").getAsLong();
        String newPwd = data.get("pwd").getAsString();
        Preconditions.checkState(StringUtils.isNotBlank(newPwd));

        BaseDboFactory<LoginUserDbo> factory = Global.getInstance().getDatabaseManager().getFactory(LoginUserDbo.class);
        LoginUserDbo login = factory.getById(loginId);
        if (login != null) {
            SecurityHelper.savePassword(login, newPwd);
            jobj = JsonHelper.getSuccessObject();
        }
        else {
            jobj = JsonHelper.getFailObject("dao.notfound", null);
        }

        return Response.ok(jobj).build();
    }
}
