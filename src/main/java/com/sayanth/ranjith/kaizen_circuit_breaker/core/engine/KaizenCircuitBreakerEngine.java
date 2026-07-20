package com.sayanth.ranjith.kaizen_circuit_breaker.core.engine;

/**
 * Executes work through a named breaker.
 */
public interface KaizenCircuitBreakerEngine {

    <T> T execute(String breakerName, KaizenInvocation<T> invocation) throws Throwable;
}
