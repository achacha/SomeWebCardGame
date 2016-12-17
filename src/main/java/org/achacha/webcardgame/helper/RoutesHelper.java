package org.achacha.webcardgame.helper;

import com.google.gson.JsonObject;

public class RoutesHelper {
    public static JsonObject getSuccessObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("success", "true");
        return obj;
    }
}
