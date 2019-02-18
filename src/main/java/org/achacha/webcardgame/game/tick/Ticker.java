package org.achacha.webcardgame.game.tick;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The big ticker
 * Maintains global tick count
 */
public class Ticker {
    private static final Ticker instance = new Ticker();

    private final AtomicLong tick;

    /**
     * @return Global ticker
     */
    public static Ticker getInstance() {
        return instance;
    }

    private Ticker() {
        this.tick = new AtomicLong();
    }

    /**
     * @return Current tick number
     */
    public long getTick() {
        return tick.get();
    }
}
