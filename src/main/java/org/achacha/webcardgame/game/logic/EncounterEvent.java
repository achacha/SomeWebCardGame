package org.achacha.webcardgame.game.logic;

import org.achacha.base.json.JsonEmittable;
import org.achacha.webcardgame.game.dbo.CardDbo;

public class EncounterEvent implements JsonEmittable {
    private final EventType type;
    private final boolean isPlayer;
    private Integer value;        // Some value (damage, etc)
    private Long id;          // ID associated with the event
    private CardDbo card;

    /**
     * Builder for player event
     * @param type EventType
     * @return Builder
     */
    public static Builder builder(EventType type) {
        return new Builder(type, true);
    }

    /**
     * Builder for event
     * @param type EventType
     * @param isPlayer if false then enemy event
     * @return Builder
     */
    public static Builder builder(EventType type, boolean isPlayer) {
        return new Builder(type, isPlayer);
    }

    enum EventType {
        Start,

        CardStart,
        CardHealth,
        CardAttack,
        CardAttackCrit,
        CardAttackAbsorb,
        CardAttackCritAbsorb,
        CardDeath,

        PlayerWin,
        PlayerDraw,
        PlayerLose
    }

    public static class Builder {
        final EncounterEvent event;
        Builder(EventType type, boolean isPlayer) {
            event = new EncounterEvent(type, isPlayer);
        }

        EncounterEvent build() {
            return event;
        }

        public Builder withValue(int value) {
            event.value = value;
            return this;
        }

        public Builder withId(long id) {
            event.id = id;
            return this;
        }

        public Builder withCard(CardDbo card) {
            event.card = card;
            return this;
        }
    }

    private EncounterEvent(EventType type, boolean isPlayer) {
        this.type = type;
        this.isPlayer = isPlayer;
    }

    public EventType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    public CardDbo getCard() {
        return card;
    }

    @Override
    public String toString() {
        return type.name()
                + (isPlayer ? "\tplayer " : "enemy ")
                + (value > 0 ? "\tvalue="+value : "")
                + (id > 0 ? "\tid="+id : "")
                + (card != null ? card : "");
    }
}
