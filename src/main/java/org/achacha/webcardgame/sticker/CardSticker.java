package org.achacha.webcardgame.sticker;

import com.google.gson.JsonObject;
import org.achacha.base.i18n.LocalizedKey;
import org.achacha.base.json.JsonEmittable;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.logic.EncounterEventLog;
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
        HOT_AT1,
        HOT_AT3,
        HOT_AT5,
        HOT_AT10,
        DOT_AT1,
        DOT_AT3,
        DOT_AT5,
        DOT_AT10
    }

    /** I18n key for title */
    protected final LocalizedKey title;
    /** I18n key for description */
    protected final LocalizedKey description;

    /**
     * There are 2 resource keys
     *   base + ".title" for the sticker title
     *   base + ".desc" for the description
     *
     * @param messageKeyBase resource key base for the description
     */
    public CardSticker(String messageKeyBase) {
        this.title = LocalizedKey.of(messageKeyBase + ".title");
        this.description = LocalizedKey.of(messageKeyBase + ".desc");
    }

    /**
     * Perform before encounter
     *
     * @param eventLog Event Log
     * @param activeCard Active card
     * @param targetCard Target card
     */
    public void beforeEncounter(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
    }

    /**
     * Preform effects during encounter, before every turn
     *
     * @param eventLog Event Log
     * @param activeCard Active card
     * @param targetCard Target card
     */
    public abstract void beforeTurn(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard);

    /**
     * Preform effects during encounter, after every turn
     *
     * @param eventLog Event Log
     * @param activeCard Active card
     * @param targetCard Target card
     */
    public abstract void afterTurn(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard);

    /**
     * Perform after end of encounter
     *
     * @param eventLog Event Log
     * @param activeCard Active card
     * @param targetCard Target card
     */
    public void afterEncounter(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("title", title)
                .append("description", description)
                .toString();
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jobj = new JsonObject();
        jobj.addProperty("title", title.toString());
        jobj.addProperty("description", description.toString());
        return jobj;
    }
}
