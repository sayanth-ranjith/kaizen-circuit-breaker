package com.sayanth.ranjith.kaizen_circuit_breaker.core.window;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Keeps the most recent N outcomes.
 */
public class CountBasedSlidingWindowStore implements SlidingWindowStore {

    private final int maxSize;
    private final Deque<Boolean> outcomes = new ArrayDeque<>();

    public CountBasedSlidingWindowStore(int maxSize) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("maxSize must be greater than zero");
        }
        this.maxSize = maxSize;
    }

    @Override
    public synchronized void recordSuccess(Instant timestamp) {
        record(true);
    }

    @Override
    public synchronized void recordFailure(Instant timestamp) {
        record(false);
    }

    @Override
    public synchronized WindowSnapshot snapshot(Instant now) {
        long failures = outcomes.stream().filter(outcome -> !outcome).count();
        return new WindowSnapshot(outcomes.size(), failures);
    }

    @Override
    public synchronized void reset() {
        outcomes.clear();
    }

    private void record(boolean success) {
        if (outcomes.size() == maxSize) {
            outcomes.removeFirst();
        }
        outcomes.addLast(success);
    }
}
