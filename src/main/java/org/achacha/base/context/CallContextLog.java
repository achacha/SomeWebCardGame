package org.achacha.base.context;

import com.google.gson.JsonObject;
import org.achacha.base.collection.FixedSizeSortedSet;
import org.achacha.base.json.JsonEmittable;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import javax.annotation.Nonnull;
import java.util.Set;

public class CallContextLog {
    private int slowThresholdInMillis = 2000;   // 2 sec
    private CircularFifoQueue<Event> eventsLog = new CircularFifoQueue<>(100);
    private FixedSizeSortedSet<Event> eventsSlow = new FixedSizeSortedSet<>(20);

    public CallContextLog() {}

    public static class Event implements JsonEmittable, Comparable<Event> {
        private String uri;
        private long createdTimeMillis;
        private long destroyedTimeMillis;

        public Event(CallContext context) {
            uri = context.getMethod() + " " + context.getRequest().getRequestURI();
            createdTimeMillis = context.getCreatedTimeMillis();
            destroyedTimeMillis = System.currentTimeMillis();
        }

        @Override
        public JsonObject toJsonObject() {
            JsonObject obj = new JsonObject();

            obj.addProperty("uri", uri);
            obj.addProperty("duration_ms", destroyedTimeMillis - createdTimeMillis);

            return obj;
        }

        @Override
        public JsonObject toJsonObjectAdmin() {
            return toJsonObject();
        }

        public String getUri() {
            return uri;
        }

        public long getCreatedTimeMillis() {
            return createdTimeMillis;
        }

        public long getDestroyedTimeMillis() {
            return destroyedTimeMillis;
        }

        public long getDurationInMillis() {
            return destroyedTimeMillis - createdTimeMillis;
        }

        @Override
        public int compareTo(@Nonnull Event that) {
            long thisDur = this.getDurationInMillis();
            long thatDur = that.getDurationInMillis();
            if (thisDur == thatDur)
                return 0;
            else
                return (thisDur<thatDur ? 1 : -1);
        }
    }

    synchronized void process(CallContext context) {
        Event event = new Event(context);
        eventsLog.add(event);
        if (event.getDurationInMillis() > slowThresholdInMillis) {
            eventsSlow.add(event);
        }
    }

    public CircularFifoQueue<Event> getEventsLog() {
        return eventsLog;
    }

    public Set<Event> getEventsSlow() {
        return eventsSlow;
    }

    public long getSlowThresholdInMillis() {
        return slowThresholdInMillis;
    }
}
