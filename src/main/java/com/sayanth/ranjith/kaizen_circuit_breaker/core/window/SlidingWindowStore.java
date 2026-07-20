package com.sayanth.ranjith.kaizen_circuit_breaker.core.window;

import java.time.Instant;

/**
 * Stores recent outcomes for a breaker.
 */
public interface SlidingWindowStore {

    void recordSuccess(Instant timestamp);

    void recordFailure(Instant timestamp);

    WindowSnapshot snapshot(Instant now);

    void reset();
}
