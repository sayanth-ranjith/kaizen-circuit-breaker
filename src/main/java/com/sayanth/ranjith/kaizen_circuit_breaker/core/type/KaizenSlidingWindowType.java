package com.sayanth.ranjith.kaizen_circuit_breaker.core.type;

/**
 * Defines how the circuit breaker should keep the recent request sample that it
 * uses for health evaluation.
 * <ul>
 *   <li>{@link #COUNT_BASED} - keeps the most recent N requests, regardless of time.</li>
 *   <li>{@link #TIME_BASED} - keeps requests that happened within a recent time range.</li>
 * </ul>
 *
 * <p>
 * A sliding window helps the breaker react to current traffic patterns instead of
 * relying on outdated history.
 * The breaker evaluates the recent sample, counts failures versus successes, and
 * compares the failure rate against the configured threshold. For example, a
 * sample like {@code F F F S S} contains 3 failures out of 5 requests, or 60%
 * failures, which would trip a breaker configured with a 50% failure threshold.
 * </p>
 */
public enum KaizenSlidingWindowType {
    COUNT_BASED,
    TIME_BASED
}
