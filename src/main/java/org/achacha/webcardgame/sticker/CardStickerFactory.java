package org.achacha.webcardgame.sticker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardStickerFactory {
    private static final Logger LOGGER = LogManager.getLogger(CardStickerFactory.class);

    /**
     * Maps a type to a concrete class implementation
     */
    private static final Map<String, CardSticker> STICKERS = new HashMap<>();

    /**
     * Initialize
     */
    static {
        STICKERS.put(CardSticker.Type.NOP.name(), new CardStickerHealing(CardSticker.Type.NOP, "ecs.nop", 0, 0, 0, 0));
        STICKERS.put(CardSticker.Type.HOT_AT1.name(), new CardStickerHealing(CardSticker.Type.HOT_AT1, "ecs.hot.at1", 0, 0, 1, 0));
        STICKERS.put(CardSticker.Type.HOT_AT3.name(), new CardStickerHealing(CardSticker.Type.HOT_AT3, "ecs.hot.at3", 0, 0, 3, 0));
        STICKERS.put(CardSticker.Type.HOT_AT5.name(), new CardStickerHealing(CardSticker.Type.HOT_AT5, "ecs.hot.at5", 0, 0, 5, 0));
        STICKERS.put(CardSticker.Type.HOT_AT10.name(), new CardStickerHealing(CardSticker.Type.HOT_AT10, "ecs.hot.at10", 0, 0, 10, 0));
        STICKERS.put(CardSticker.Type.DOT_AT1.name(), new CardStickerDamage(CardSticker.Type.DOT_AT1, "ecs.dot.at1", 0, 0, 1, 0));
        STICKERS.put(CardSticker.Type.DOT_AT3.name(), new CardStickerDamage(CardSticker.Type.DOT_AT3, "ecs.dot.at3", 0, 0, 3, 0));
        STICKERS.put(CardSticker.Type.DOT_AT5.name(), new CardStickerDamage(CardSticker.Type.DOT_AT5, "ecs.dot.at5", 0, 0, 5, 0));
        STICKERS.put(CardSticker.Type.DOT_AT10.name(), new CardStickerDamage(CardSticker.Type.DOT_AT10, "ecs.dot.at10", 0, 0, 10, 0));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<CardSticker> stickers = new ArrayList<>();

        public List<CardSticker> build() {
            return stickers;
        }

        public Builder add(CardSticker.Type type) {
            stickers.add(getSticker(type));
            return this;
        }
    }


    /**
     * Lookup sticker implementation by name
     * @param name Sticker name
     * @return CardSticker or null if not found
     */
    @Nullable
    public static CardSticker getSticker(String name) {
        CardSticker sticker = STICKERS.get(name);
        if (sticker == null) {
            LOGGER.warn("Sticker not found: "+name);
        }
        return sticker;
    }

    /**
     * Lookup sticker by enum type
     * @param type Type
     * @return CardSticker
     */
    @Nonnull
    public static CardSticker getSticker(CardSticker.Type type) {
        return STICKERS.get(type.name());
    }
}
