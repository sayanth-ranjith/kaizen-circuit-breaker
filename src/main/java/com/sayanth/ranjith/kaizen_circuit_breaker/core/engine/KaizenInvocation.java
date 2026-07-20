package com.sayanth.ranjith.kaizen_circuit_breaker.core.engine;

/**
 * Functional boundary for protected work that may throw checked exceptions.
 */
@FunctionalInterface
public interface KaizenInvocation<T> {

    T proceed() throws Throwable;
}

