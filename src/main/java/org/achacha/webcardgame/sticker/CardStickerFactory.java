package org.achacha.webcardgame.sticker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
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
        STICKERS.put(CardSticker.Type.NOP.name(), new CardStickerHealing("ecs.nop", 0, 0, 0, 0));
        STICKERS.put(CardSticker.Type.HOT_MINOR.name(), new CardStickerHealing("ecs.hot.minor", 0, 0, 10, 0));
        STICKERS.put(CardSticker.Type.HOT_MAJOR.name(), new CardStickerHealing("ecs.hot.major", 0, 0, 25, 0));
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
