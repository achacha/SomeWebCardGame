package org.achacha.webcardgame.game.tick;

import java.sql.Timestamp;

public class TickHelper {
    /**
     * Seconds per tick
     */
    public static final long SECONDS_PER_TICK = 60;

    /**
     *
     * @param startTs
     * @param endTs
     * @return
     */
    public static long ticksBetweenTimestamps(Timestamp startTs, Timestamp endTs) {
        long diffInSeconds = (endTs.getTime() - startTs.getTime()) / 1000;
        return diffInSeconds / SECONDS_PER_TICK;
    }
}
