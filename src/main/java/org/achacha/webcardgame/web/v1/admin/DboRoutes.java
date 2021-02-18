package org.achacha.webcardgame.web.v1.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.achacha.base.db.AdminDboFactory;
import org.achacha.base.db.BaseDbo;
import org.achacha.base.db.DboClassHelper;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.Optional;
import java.util.Set;

/**
 * /api/admin/dbo
 */
@Path("admin/dbo")
@Produces(MediaType.APPLICATION_JSON)
public class DboRoutes {
    private static final Logger LOGGER = LogManager.getLogger(DboRoutes.class);

    /**
     * Get all available indexed Dbos
     */
    @Path("all")
    @GET
    @SecurityLevelRequired(SecurityLevel.ADMIN)
    public Response allDbos() {
        JsonObject obj = JsonHelper.getSuccessObject();

        JsonArray ary = new JsonArray();
        DboClassHelper.getIndexedDboClasses().stream()
                .map(Class::getSimpleName)
                .forEach(ary::add);
        obj.add("data", ary);

        return Response.ok().entity(obj).build();

    }

    /**
     * Get all available ids for the Dao table
     * Can return a lot of data, this call is admin only
     * @param simpleName Dao simple name that maps to Dao class (usually from the 'all' call above
     */
    @GET
    @Path("/{name}/ids")
    @SecurityLevelRequired(SecurityLevel.ADMIN)
    public Response getAllObjectIds(@PathParam("name") String simpleName) {
        Set<Class<? extends BaseDbo>> indexedDbos = DboClassHelper.getIndexedDboClasses();
        Optional<Class<? extends BaseDbo>> clz = indexedDbos.stream().filter(cls->cls.getSimpleName().equals(simpleName)).findFirst();
        if (clz.isPresent()) {
            Set<Long> ids = AdminDboFactory.getAllIds(clz.get());

            return Response.ok(
                    JsonHelper.getSuccessObject(null, ids)
            ).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("Dbo not found", simpleName)).build();
        }
    }

    /**
     * Get JSON for Dao with given id
     * @param simpleName Simple DAO name to lookup
     * @param id Object id
     * @return JSON
     */
    @GET
    @Path("/{name}/{id}")
    @SecurityLevelRequired(SecurityLevel.ADMIN)
    public Response getObjectData(@PathParam("name") String simpleName, @PathParam("id") long id) {
        Set<Class<? extends BaseDbo>> indexedDbos = DboClassHelper.getIndexedDboClasses();
        Optional<Class<? extends BaseDbo>> clz = indexedDbos.stream().filter(cls->cls.getSimpleName().equals(simpleName)).findFirst();
        if (clz.isPresent()) {
            try (Connection connection = Global.getInstance().getDatabaseManager().getConnection()) {
                BaseDbo dbo = Global.getInstance().getDatabaseManager().getFactory(clz.get()).getById(connection, id);
                if (dbo != null)
                    return Response.ok(
                            JsonHelper.getSuccessObject(simpleName, dbo)
                    ).build();
            }
            catch(Exception e) {
                LOGGER.error("Failed to get dboClass="+clz.get()+" id="+id, e);
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("Dbo not found", simpleName)).build();
    }
}
