package org.achacha.base.logging;

import javax.annotation.Nullable;

/**
 * Event object used in EventLogDbo logging
 *
 * NOTE:
 * DO NOT CHANGE IDs if `event_log` table has data without updating it also
 * IDs must start with 0
 */
public enum Event {
    LOGIN(0, "login"),
    LOGOUT(1, "logout"),
    LOGIN_FAIL(2, "fail"),
    LOGIN_IMPERSONATE(3, "impersonate"),
    LOGIN_PERMISSION_INVALID(4, "permission.invalid");

    private int id;
    private String resourceKey;

    Event(int id, String resourceName) {
        this.id = id;
        this.resourceKey = resourceName;
    }

    /**
     * @return event id
     */
    public int getId() {
        return id;
    }

    /**
     * @return resource key
     */
    public String getResourceKey() { return resourceKey; }

    /**
     * Lookup Event enum by id
     * @param id id
     * @return Event enum or null if not valid id
     */
    @Nullable
    public static Event valueOf(int id) {
        if (id <0 || id >= values().length)
            return null;

        return values()[id];
    }
}
