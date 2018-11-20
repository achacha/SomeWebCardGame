package org.achacha.webcardgame.sticker;

import com.google.gson.JsonObject;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.logic.EncounterEvent;
import org.achacha.webcardgame.game.logic.EncounterEventLog;
import org.achacha.webcardgame.game.logic.EventType;

public class CardStickerDamage extends CardSticker {
    private final int beforeEncounterDamage;
    private final int beforeTurnDamage;
    private final int afterTurnDamage;
    private final int afterEncounterDamage;


    /**
     * Damaging
     * NOTE: All damaging values are percent of total and must be [0,100]
     *
     * @param messageKeyBase message key base
     * @param beforeEncounterDamage before encounter
     * @param beforeTurnDamage before each turn
     * @param afterTurnDamage after each turn
     * @param afterEncounterDamage after encounter
     */
    public CardStickerDamage(String messageKeyBase, int beforeEncounterDamage, int beforeTurnDamage, int afterTurnDamage, int afterEncounterDamage) {
        super(messageKeyBase);

        this.beforeEncounterDamage = beforeEncounterDamage;
        this.beforeTurnDamage = beforeTurnDamage;
        this.afterTurnDamage = afterTurnDamage;
        this.afterEncounterDamage = afterEncounterDamage;
    }

    @Override
    public void beforeEncounter(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (beforeEncounterDamage > 0) {
            targetCard.decHealth(beforeEncounterDamage);
            eventLog.add(EncounterEvent.builder(EventType.StickerDamage, false).withValue(beforeEncounterDamage).build());
        }
    }

    @Override
    public void beforeTurn(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (beforeTurnDamage > 0) {
            targetCard.decHealth(beforeTurnDamage);
            eventLog.add(EncounterEvent.builder(EventType.StickerDamage, false).withValue(beforeTurnDamage).build());
        }
    }

    @Override
    public void afterTurn(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (afterTurnDamage > 0) {
            targetCard.decHealth(afterTurnDamage);
            eventLog.add(EncounterEvent.builder(EventType.StickerDamage, false).withValue(afterTurnDamage).build());
        }
    }

    @Override
    public void afterEncounter(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (afterEncounterDamage > 0) {
            targetCard.decHealth(afterEncounterDamage);
            eventLog.add(EncounterEvent.builder(EventType.StickerDamage, false).withValue(afterEncounterDamage).build());
        }
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jobj = super.toJsonObject();
        jobj.addProperty("beforeEncounterDamage", beforeEncounterDamage);
        jobj.addProperty("beforeTurnDamage", beforeTurnDamage);
        jobj.addProperty("afterEncounterDamage", afterEncounterDamage);
        jobj.addProperty("afterTurnDamage", afterTurnDamage);
        return jobj;
    }
}
