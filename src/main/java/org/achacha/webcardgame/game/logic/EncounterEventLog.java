package org.achacha.webcardgame.game.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.achacha.base.json.JsonEmittable;
import org.achacha.webcardgame.game.dbo.EncounterDbo;
import org.achacha.webcardgame.game.dbo.PlayerDbo;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class EncounterEventLog implements JsonEmittable {

    private final PlayerDbo player;
    private final EncounterDbo encounter;
    private Deque<EncounterEvent> events = new LinkedList<>();

    public EncounterEventLog(PlayerDbo player, EncounterDbo encounter) {
        this.player = player;
        this.encounter = encounter;
    }

    public void add(EncounterEvent event) {
        events.add(event);
    }

    public Deque<EncounterEvent> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return events.stream().map(EncounterEvent::toString).collect(Collectors.joining("\n"));
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject jobj = new JsonObject();
        jobj.add("player", player.toJsonObject());
        jobj.add("encounter", encounter.toJsonObject());
        jobj.add("events", events.stream().map(EncounterEvent::toJsonObject).collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
        return jobj;
    }

    public void add(DamagePerTurn dpt) {
        byte flags = dpt.getBitSet().toByteArray()[0];
        switch(flags) {
            case 0x0:
                // 000 Enemy attacking
                events.add(EncounterEvent.builder(EventType.CardAttack, false).withValue(dpt.getDamage()).build());
                break;
            case 0x2:
                // 010 Critical, Enemy attacking
                events.add(EncounterEvent.builder(EventType.CardAttackCrit, false).withValue(dpt.getDamage()).build());
                break;
            case 0x4:
                // 100 Absorb, Enemy attacking
                events.add(EncounterEvent.builder(EventType.CardAttackAbsorb, false).withValue(dpt.getDamage()).build());
                break;
            case 0x6:
                // 110 Absorb, Critical, Enemy attacking
                events.add(EncounterEvent.builder(EventType.CardAttackCritAbsorb, false).withValue(dpt.getDamage()).build());
                break;

            case 0x1:
                // 001 Player attacking
                events.add(EncounterEvent.builder(EventType.CardAttack).withValue(dpt.getDamage()).build());
                break;
            case 0x3:
                // 011 Critical, Player attacking
                events.add(EncounterEvent.builder(EventType.CardAttackCrit).withValue(dpt.getDamage()).build());
                break;
            case 0x5:
                // 101 Absorb, Player attacking
                events.add(EncounterEvent.builder(EventType.CardAttackAbsorb).withValue(dpt.getDamage()).build());
                break;
            case 0x7:
                // 111 Absorb, Critical, Player attacking
                events.add(EncounterEvent.builder(EventType.CardAttackCritAbsorb).withValue(dpt.getDamage()).build());
                break;
            default:
                throw new RuntimeException("Unmapped event: flags="+flags+" from dpt="+dpt);
        }
    }
}
