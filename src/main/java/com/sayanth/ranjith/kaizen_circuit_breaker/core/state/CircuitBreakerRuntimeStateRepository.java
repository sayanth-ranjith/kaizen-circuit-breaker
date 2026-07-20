package com.sayanth.ranjith.kaizen_circuit_breaker.core.state;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.config.KaizenConfig;

/**
 * Stores per-breaker runtime state.
 */
public interface CircuitBreakerRuntimeStateRepository {

    BreakerRuntimeContext getOrCreate(String name, KaizenConfig config);
}
