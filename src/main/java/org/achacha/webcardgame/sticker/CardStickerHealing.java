package org.achacha.webcardgame.sticker;

import com.google.gson.JsonObject;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.logic.EncounterEvent;
import org.achacha.webcardgame.game.logic.EncounterEventLog;
import org.achacha.webcardgame.game.logic.EventType;

public class CardStickerHealing extends CardSticker {
    private final int beforeEncounterHeal;
    private final int beforeTurnHeal;
    private final int afterTurnHeal;
    private final int afterEncounterHeal;


    /**
     * Healing
     * NOTE: All healing values are percent of total and must be [0,100]
     *
     * @param messageKeyBase message key base
     * @param beforeEncounterHeal before encounter
     * @param beforeTurnHeal before each turn
     * @param afterTurnHeal after each turn
     * @param afterEncounterHeal after encounter
     */
    public CardStickerHealing(String messageKeyBase, int beforeEncounterHeal, int beforeTurnHeal, int afterTurnHeal, int afterEncounterHeal) {
        super(messageKeyBase);

        this.beforeEncounterHeal = beforeEncounterHeal;
        this.beforeTurnHeal = beforeTurnHeal;
        this.afterTurnHeal = afterTurnHeal;
        this.afterEncounterHeal = afterEncounterHeal;
    }

    @Override
    public void beforeEncounter(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (beforeEncounterHeal > 0) {
            activeCard.incHealth(beforeEncounterHeal);
            eventLog.add(EncounterEvent.builder(EventType.StickerHeal).withValue(beforeEncounterHeal).build());
        }
    }

    @Override
    public void beforeTurn(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (beforeTurnHeal > 0) {
            activeCard.incHealth(beforeTurnHeal);
            eventLog.add(EncounterEvent.builder(EventType.StickerHeal).withValue(beforeTurnHeal).build());
        }
    }

    @Override
    public void afterTurn(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (afterTurnHeal > 0) {
            activeCard.incHealth(afterTurnHeal);
            eventLog.add(EncounterEvent.builder(EventType.StickerHeal).withValue(afterTurnHeal).build());
        }
    }

    @Override
    public void afterEncounter(EncounterEventLog eventLog, CardDbo activeCard, CardDbo targetCard) {
        if (afterEncounterHeal > 0) {
            activeCard.incHealth(afterEncounterHeal);
            eventLog.add(EncounterEvent.builder(EventType.StickerHeal).withValue(afterEncounterHeal).build());
        }
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jobj = super.toJsonObject();
        jobj.addProperty("beforeEncounterHeal", beforeEncounterHeal);
        jobj.addProperty("beforeTurnHeal", beforeTurnHeal);
        jobj.addProperty("afterEncounterHeal", afterEncounterHeal);
        jobj.addProperty("afterTurnHeal", afterTurnHeal);
        return jobj;
    }
}
