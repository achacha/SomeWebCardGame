package org.achacha.webcardgame.sticker;

import com.google.gson.JsonObject;
import org.achacha.webcardgame.game.dbo.CardDbo;
import org.achacha.webcardgame.game.dbo.EncounterDbo;

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
    public void beforeEncounter(CardDbo activeCard, EncounterDbo encounter) {
        activeCard.incHealth(beforeEncounterHeal);
    }

    @Override
    public void beforeTurn(CardDbo activeCard, EncounterDbo encounter) {
        activeCard.incHealth(beforeTurnHeal);
    }

    @Override
    public void afterTurn(CardDbo activeCard, EncounterDbo encounter) {
        activeCard.incHealth(afterTurnHeal);
    }

    @Override
    public void afterEncounter(CardDbo activeCard, EncounterDbo encounter) {
        activeCard.incHealth(afterEncounterHeal);
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
