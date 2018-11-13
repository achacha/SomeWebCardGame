package org.achacha.webcardgame.sticker;

import com.google.gson.JsonObject;
import org.achacha.base.i18n.LocalizedKey;
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
