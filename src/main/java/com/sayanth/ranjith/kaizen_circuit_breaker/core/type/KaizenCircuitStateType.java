package com.sayanth.ranjith.kaizen_circuit_breaker.core.type;

/**
 * Represents the operational states of a circuit breaker.
 * <ul>
 *   <li>{@link #CLOSED} - requests are allowed to pass through normally.</li>
 *   <li>{@link #OPEN} - requests are blocked because failures exceeded the threshold.</li>
 *   <li>{@link #HALF_OPEN} - a limited number of probe requests are allowed to test recovery.</li>
 * </ul>
 *
 * @author Sayanth P V
 */
public enum KaizenCircuitStateType {
    CLOSED,
    HALF_OPEN,
    OPEN
}
