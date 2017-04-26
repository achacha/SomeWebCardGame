package org.achacha.base.logging;

import org.junit.Assert;
import org.junit.Test;

public class TestEvent {
    @Test
    public void testEvent() {
        // Lookup by id
        Assert.assertEquals(Event.valueOf(Event.LOGIN.getId()), Event.LOGIN);
        Assert.assertEquals(Event.valueOf(Event.LOGIN_FAIL.getId()), Event.LOGIN_FAIL);
        Assert.assertEquals(Event.valueOf(Event.LOGIN_IMPERSONATE.getId()), Event.LOGIN_IMPERSONATE);
        Assert.assertEquals(Event.valueOf(Event.LOGIN_PERMISSION_INVALID.getId()), Event.LOGIN_PERMISSION_INVALID);
        Assert.assertEquals(null, Event.valueOf(50));
        Assert.assertEquals(null, Event.valueOf(-1));
    }
}
