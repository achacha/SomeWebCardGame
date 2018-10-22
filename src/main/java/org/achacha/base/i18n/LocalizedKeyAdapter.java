package org.achacha.base.i18n;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Serializes using Gson into JSON string and intended to be read-only
 */
public class LocalizedKeyAdapter<T extends LocalizedKeyResource> implements JsonSerializer<T> {
    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(UIMessageHelper.getInstance().getLocalizedMsg(src.getKey()));
    }
}
