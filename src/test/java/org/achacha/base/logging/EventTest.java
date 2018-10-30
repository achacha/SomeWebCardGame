package org.achacha.base.logging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EventTest {
    @Test
    void testEvent() {
        // Lookup by id
        assertEquals(Event.valueOf(Event.LOGIN.getId()), Event.LOGIN);
        assertEquals(Event.valueOf(Event.LOGIN_FAIL.getId()), Event.LOGIN_FAIL);
        assertEquals(Event.valueOf(Event.LOGIN_IMPERSONATE.getId()), Event.LOGIN_IMPERSONATE);
        assertEquals(Event.valueOf(Event.LOGIN_PERMISSION_INVALID.getId()), Event.LOGIN_PERMISSION_INVALID);
        assertNull(Event.valueOf(50));
        assertNull(Event.valueOf(-1));
    }
}
