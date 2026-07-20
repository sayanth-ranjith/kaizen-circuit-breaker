package com.sayanth.ranjith.kaizen_circuit_breaker.core.window;

/**
 * Immutable snapshot of the current sliding window.
 */
public record WindowSnapshot(long totalCalls, long failureCalls) {

    public double failureRatePercent() {
        if (totalCalls == 0) {
            return 0.0d;
        }
        return (failureCalls * 100.0d) / totalCalls;
    }
}
