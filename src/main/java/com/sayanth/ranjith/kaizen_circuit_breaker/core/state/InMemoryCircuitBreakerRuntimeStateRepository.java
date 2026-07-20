package com.sayanth.ranjith.kaizen_circuit_breaker.core.state;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.config.KaizenConfig;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.window.SlidingWindowStoreFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory runtime state storage.
 */
@Component
public class InMemoryCircuitBreakerRuntimeStateRepository implements CircuitBreakerRuntimeStateRepository {

    private final Map<String, BreakerRuntimeContext> states = new ConcurrentHashMap<>();
    private final SlidingWindowStoreFactory windowStoreFactory;

    public InMemoryCircuitBreakerRuntimeStateRepository(SlidingWindowStoreFactory windowStoreFactory) {
        this.windowStoreFactory = windowStoreFactory;
    }

    @Override
    public BreakerRuntimeContext getOrCreate(String name, KaizenConfig config) {
        return states.computeIfAbsent(
                name,
                key -> new BreakerRuntimeContext(
                        key,
                        config,
                        windowStoreFactory.create(config)
                )
        );
    }
}
