package com.sayanth.ranjith.kaizen_circuit_breaker.core.state;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.config.KaizenConfig;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.type.KaizenCircuitStateType;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.window.SlidingWindowStore;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.window.WindowSnapshot;
import com.sayanth.ranjith.kaizen_circuit_breaker.exception.KaizenCircuitBreakerOpenException;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Mutable runtime state for one configured breaker.
 */
public final class BreakerRuntimeContext {

    private final String breakerName;
    private final KaizenConfig config;
    private final SlidingWindowStore windowStore;

    private KaizenCircuitStateType state = KaizenCircuitStateType.CLOSED;
    private Instant openSince;
    private int halfOpenProbeCount;
    private int halfOpenSuccessCount;

    public BreakerRuntimeContext(String breakerName, KaizenConfig config, SlidingWindowStore windowStore) {
        this.breakerName = normalizeName(breakerName);
        this.config = Objects.requireNonNull(config, "config");
        this.windowStore = Objects.requireNonNull(windowStore, "windowStore");
    }

    public synchronized void beforeInvocation(Instant now) {
        Objects.requireNonNull(now, "now");
        if (state == KaizenCircuitStateType.OPEN) {
            if (!isWaitDurationElapsed(now)) {
                throw openException();
            }
            transitionToHalfOpen();
        }
        if (state == KaizenCircuitStateType.HALF_OPEN) {
            if (halfOpenProbeCount >= config.permittedCallsInHalfOpenState()) {
                throw openException();
            }
            halfOpenProbeCount++;
        }
    }

    public synchronized void recordSuccess(Instant now) {
        Objects.requireNonNull(now, "now");
        if (state == KaizenCircuitStateType.CLOSED) {
            windowStore.recordSuccess(now);
            evaluateClosedState(now);
            return;
        }
        if (state == KaizenCircuitStateType.HALF_OPEN) {
            halfOpenSuccessCount++;
            if (halfOpenSuccessCount >= config.permittedCallsInHalfOpenState()) {
                transitionToClosed();
            }
        }
    }

    public synchronized void recordFailure(Instant now) {
        Objects.requireNonNull(now, "now");
        if (state == KaizenCircuitStateType.CLOSED) {
            windowStore.recordFailure(now);
            evaluateClosedState(now);
            return;
        }
        if (state == KaizenCircuitStateType.HALF_OPEN) {
            transitionToOpen(now);
        }
    }

    public synchronized KaizenCircuitStateType currentState() {
        return state;
    }

    public synchronized WindowSnapshot currentSnapshot(Instant now) {
        return windowStore.snapshot(now);
    }

    private void evaluateClosedState(Instant now) {
        WindowSnapshot snapshot = windowStore.snapshot(now);
        if (snapshot.totalCalls() < config.minimumNumberOfCalls()) {
            return;
        }
        if (snapshot.failureRatePercent() >= config.failureRateThreshold()) {
            transitionToOpen(now);
        }
    }

    private boolean isWaitDurationElapsed(Instant now) {
        return openSince != null
                && Duration.between(openSince, now).compareTo(config.waitDurationInOpenState()) >= 0;
    }

    private void transitionToHalfOpen() {
        state = KaizenCircuitStateType.HALF_OPEN;
        halfOpenProbeCount = 0;
        halfOpenSuccessCount = 0;
    }

    private void transitionToOpen(Instant now) {
        state = KaizenCircuitStateType.OPEN;
        openSince = now;
        halfOpenProbeCount = 0;
        halfOpenSuccessCount = 0;
        windowStore.reset();
    }

    private void transitionToClosed() {
        state = KaizenCircuitStateType.CLOSED;
        openSince = null;
        halfOpenProbeCount = 0;
        halfOpenSuccessCount = 0;
        windowStore.reset();
    }

    private KaizenCircuitBreakerOpenException openException() {
        return new KaizenCircuitBreakerOpenException(
                "Circuit breaker '" + breakerName + "' is OPEN"
        );
    }

    private static String normalizeName(String name) {
        String trimmed = Objects.requireNonNull(name, "breakerName").trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("breakerName must not be blank");
        }
        return trimmed;
    }
}
