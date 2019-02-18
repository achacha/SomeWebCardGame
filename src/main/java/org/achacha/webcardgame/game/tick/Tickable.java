package org.achacha.webcardgame.game.tick;

public interface Tickable {
    /**
     * Advance tickable object to the next tick
     */
    void tick();

    /**
     * By default can tick indefinately
     * @return true if we have completed the process and ticks will no longer alter the state
     */
    default boolean isComplete() {
        return false;
    }

    /**
     * By default do nothing
     */
    default void onComplete() {
    }
}
