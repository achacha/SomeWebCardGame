package org.achacha.webcardgame.sticker;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Adapter for Gson to serialize/Deserialize CardSticker
 */
public class CardStickerAdapter implements JsonSerializer<CardSticker>, JsonDeserializer<CardSticker> {
    @Override
    public JsonElement serialize(CardSticker src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getTypeName());
    }

    @Override
    public CardSticker deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String typeName = json.getAsString();
        return CardStickerFactory.getSticker(typeName);
    }
}
