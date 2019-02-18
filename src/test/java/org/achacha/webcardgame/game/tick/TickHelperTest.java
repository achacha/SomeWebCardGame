package org.achacha.webcardgame.game.tick;

import org.achacha.test.BaseInitializedTest;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TickHelperTest extends BaseInitializedTest {

    @Test
    void ticksBetweenTimestamps() {
        long millisNow = System.currentTimeMillis();
        Timestamp now0 = new Timestamp(millisNow);
        Timestamp now1 = new Timestamp(millisNow + 60 * 1000 + 1);  // Guaranteed at least 1 tick
        assertEquals(1, TickHelper.ticksBetweenTimestamps(now0, now1));
    }
}