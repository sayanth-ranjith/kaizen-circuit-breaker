package com.sayanth.ranjith.kaizen_circuit_breaker.core.window;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Keeps outcomes that happened within the configured time range.
 */
public class TimeBasedSlidingWindowStore implements SlidingWindowStore {

    private final Duration windowSize;
    private final Deque<Outcome> outcomes = new ArrayDeque<>();

    public TimeBasedSlidingWindowStore(Duration windowSize) {
        if (windowSize == null) {
            throw new IllegalArgumentException("windowSize must not be null");
        }
        if (windowSize.isZero() || windowSize.isNegative()) {
            throw new IllegalArgumentException("windowSize must be positive");
        }
        this.windowSize = windowSize;
    }

    @Override
    public synchronized void recordSuccess(Instant timestamp) {
        record(new Outcome(timestamp, true));
    }

    @Override
    public synchronized void recordFailure(Instant timestamp) {
        record(new Outcome(timestamp, false));
    }

    @Override
    public synchronized WindowSnapshot snapshot(Instant now) {
        prune(now);
        long failures = outcomes.stream().filter(outcome -> !outcome.success()).count();
        return new WindowSnapshot(outcomes.size(), failures);
    }

    @Override
    public synchronized void reset() {
        outcomes.clear();
    }

    private void record(Outcome outcome) {
        outcomes.addLast(outcome);
        prune(outcome.timestamp());
    }

    private void prune(Instant now) {
        Instant cutoff = now.minus(windowSize);
        while (!outcomes.isEmpty() && outcomes.peekFirst().timestamp().isBefore(cutoff)) {
            outcomes.removeFirst();
        }
    }

    private record Outcome(Instant timestamp, boolean success) {
    }
}
