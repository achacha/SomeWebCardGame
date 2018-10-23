package org.achacha.webcardgame.web.v1.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.achacha.base.db.AdminDboFactory;
import org.achacha.base.db.BaseIndexedDbo;
import org.achacha.base.db.DboHelper;
import org.achacha.base.global.Global;
import org.achacha.base.json.JsonHelper;
import org.achacha.base.security.SecurityLevel;
import org.achacha.webcardgame.web.filter.SecurityLevelRequired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.Set;

/**
 * /api/admin/dbo
 */
@Path("admin/dbo")
@Produces(MediaType.APPLICATION_JSON)
public class DboRoutes {

    /**
     * Get all available indexed Dbos
     */
    @Path("all")
    @GET
    public Response allDbos() {
        JsonObject obj = JsonHelper.getSuccessObject();

        JsonArray ary = new JsonArray();
        DboHelper.getIndexedDboClasses().stream()
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
        Set<Class<? extends BaseIndexedDbo>> indexedDbos = DboHelper.getIndexedDboClasses();
        Optional<Class<? extends BaseIndexedDbo>> clz = indexedDbos.stream().filter(cls->cls.getSimpleName().equals(simpleName)).findFirst();
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
        Set<Class<? extends BaseIndexedDbo>> indexedDbos = DboHelper.getIndexedDboClasses();
        Optional<Class<? extends BaseIndexedDbo>> clz = indexedDbos.stream().filter(cls->cls.getSimpleName().equals(simpleName)).findFirst();
        if (clz.isPresent()) {
            BaseIndexedDbo dbo = Global.getInstance().getDatabaseManager().byId(clz.get(), id);
            if (dbo != null)
                return Response.ok(
                        JsonHelper.getSuccessObject(simpleName, dbo)
                ).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(JsonHelper.getFailObject("Dbo not found", simpleName)).build();
    }
}
