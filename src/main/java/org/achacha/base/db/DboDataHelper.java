package org.achacha.base.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DboDataHelper {

    /**
     * Convert JsonArray of JsonObjects that map into T
     *
     * @param clz Class of Dbo
     * @param ary JSON array
     * @return List of Dbos
     */
    @Nonnull
    public static <T extends BaseDbo> List<T> from(Class<T> clz, JsonArray ary) {
        if (ary.size() > 0) {
            List<T> result = new ArrayList<>(ary.size());
            for (int i = 0; i < ary.size(); ++i) {
                JsonObject jobj = ary.get(i).getAsJsonObject();
                T dbo = T.from(jobj, clz);
                result.add(dbo);
            }
            return result;
        }
        return Collections.emptyList();
    }
}
