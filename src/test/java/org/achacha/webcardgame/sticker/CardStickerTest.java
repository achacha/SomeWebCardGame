package org.achacha.webcardgame.sticker;

import org.achacha.base.global.Global;
import org.achacha.test.BaseInitializedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardStickerTest extends BaseInitializedTest {

    public static class StickerHolder {
        List<CardSticker> stickers = new ArrayList<>();
    }

    @Test
    void toFromJsonObject() {
        StickerHolder holder = new StickerHolder();
        holder.stickers.add(CardStickerFactory.getSticker(CardSticker.Type.HOT_AT3));
        holder.stickers.add(CardStickerFactory.getSticker(CardSticker.Type.DOT_AT10));

        String json = Global.getInstance().getGson().toJson(holder);
        assertEquals("{\"stickers\":[\"HOT_AT3\",\"DOT_AT10\"]}", json);

        StickerHolder deHolder = Global.getInstance().getGson().fromJson(json, StickerHolder.class);
        assertEquals(Global.getInstance().getGson().toJson(deHolder), json);
    }
}