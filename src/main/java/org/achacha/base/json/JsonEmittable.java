package org.achacha.base.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.achacha.base.global.Global;

public interface JsonEmittable {

    /**
     * Default will call toJsonObject if not immplemented
     * This is meant for adding extra data not for public use in admin usage
     * @return Full JSON object intended for admin use
     */
    default JsonObject toJsonObjectAdmin() {
        return toJsonObject();
    }

    /**
     * Return object in JSON object representation
     * It does not guarantees that JSON object can be used to rebuild original Object
     * @return JSONObject
     */
    default JsonObject toJsonObject() {
        Class<?> clazz = getClass();
        JsonElement rootElement = Global.getInstance().getGson().toJsonTree(this, clazz);
        return rootElement.getAsJsonObject();
    }
}
