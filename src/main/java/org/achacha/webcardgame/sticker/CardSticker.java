package org.achacha.webcardgame.sticker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.achacha.base.global.Global;
import org.achacha.base.i18n.UIMessageHelper;
import org.achacha.base.json.JsonEmittable;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stickers are actions performed per encounter
 */
public abstract class CardSticker implements JsonEmittable {
    protected static final Logger LOGGER = LogManager.getLogger(CardSticker.class);

    public enum Type {
        NOP,
        HOT_MINOR,
        HOT_MAJOR
    }

    /** I18n key for title */
    protected final String titleKey;
    /** I18n key for description */
    protected final String descriptionKey;

    /**
     * There are 2 resource keys
     *   base + ".title" for the sticker title
     *   base + ".desc" for the description
     *
     * @param messageKeyBase resource key base for the description
     */
    public CardSticker(String messageKeyBase) {
        this.titleKey = messageKeyBase + ".title";
        this.descriptionKey = messageKeyBase + ".desc";
    }

    /**
     * @return Resource key for description
     */
    public String getDescriptionKey() {
        return this.descriptionKey;
    }

    /**
     * @return Resource key for title
     */
    public String getTitleKey() {
        return titleKey;
    }

    /**
     * Perform before encounter
     *
     * @param encounter EncounterDbo associated with this sticker
     * @param activeCard Active card
     */
    public void beforeEncounter(CardDbo activeCard, EncounterDbo encounter) {
    }

    /**
     * Preform effects during encounter, before every turn
     *
     * @param activeCard Active card
     * @param encounter EncounterDbo associated with this sticker
     */
    public abstract void beforeTurn(CardDbo activeCard, EncounterDbo encounter);

    /**
     * Preform effects during encounter, after every turn
     *
     * @param activeCard Active card
     * @param encounter EncounterDbo associated with this sticker
     */
    public abstract void afterTurn(CardDbo activeCard, EncounterDbo encounter);

    /**
     * Perform after end of encounter
     *
     * @param activeCard Active card
     * @param encounter EncounterDbo associated with this sticker
     */
    public void afterEncounter(CardDbo activeCard, EncounterDbo encounter) {
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("titleKey", titleKey)
                .append("descriptionKey", descriptionKey)
                .toString();
    }

    @Override
    public JsonObject toJsonObject() {
        Class clazz = getClass();
        JsonElement rootElement = Global.getInstance().getGson().toJsonTree(this, clazz);
        JsonObject jobj = rootElement.getAsJsonObject();
        jobj.addProperty("title", UIMessageHelper.getInstance().getLocalizedMsg(titleKey));
        jobj.addProperty("description", UIMessageHelper.getInstance().getLocalizedMsg(descriptionKey));
        return jobj;
    }
}
