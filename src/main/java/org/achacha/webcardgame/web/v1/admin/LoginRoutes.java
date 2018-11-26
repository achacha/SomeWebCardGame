package org.achacha.webcardgame.web.v1.admin;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.achacha.base.db.BaseDboFactory;
import org.achacha.base.db.DatabaseManager;
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
import java.sql.Connection;
import java.sql.SQLException;

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

        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        BaseDboFactory<LoginUserDbo> factory = dbm.getFactory(LoginUserDbo.class);
        LoginUserDbo login = null;
        try (Connection connection = dbm.getConnection()) {
            login = factory.getById(connection, id);
        } catch (SQLException e) {
            LOGGER.error("Failed to get user, id="+id, e);
        }

        if (login != null)
            return Response.ok(login).build();
        else
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("dao.notfound", null)).build();
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

        DatabaseManager dbm = Global.getInstance().getDatabaseManager();
        BaseDboFactory<LoginUserDbo> factory = dbm.getFactory(LoginUserDbo.class);
        try (Connection connection = dbm.getConnection()) {
            LoginUserDbo login = factory.getById(connection, loginId);

            if (login != null) {
                SecurityHelper.savePassword(connection, login, newPwd);
                jobj = JsonHelper.getSuccessObject();
            }
            else {
                jobj = JsonHelper.getFailObject("dao.notfound", null);
            }
            connection.commit();

            return Response.ok(jobj).build();
        } catch (Exception e) {
            LOGGER.error("Failed to update password for user, id="+loginId, e);
        }
        return Response.serverError().build();
    }
}
