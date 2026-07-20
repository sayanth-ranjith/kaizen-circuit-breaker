package com.sayanth.ranjith.kaizen_circuit_breaker.core.engine;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.config.KaizenConfig;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.config.KaizenConfigRegistry;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.state.BreakerRuntimeContext;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.state.CircuitBreakerRuntimeStateRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/**
 * Default orchestration layer for breaker-protected execution.
 */
@Component
public class DefaultKaizenCircuitBreakerEngine implements KaizenCircuitBreakerEngine {

    private final KaizenConfigRegistry configRegistry;
    private final CircuitBreakerRuntimeStateRepository stateRepository;
    private final Clock clock;

    public DefaultKaizenCircuitBreakerEngine(KaizenConfigRegistry configRegistry,
                                             CircuitBreakerRuntimeStateRepository stateRepository,
                                             Clock clock) {
        this.configRegistry = configRegistry;
        this.stateRepository = stateRepository;
        this.clock = clock;
    }

    @Override
    public <T> T execute(String breakerName, KaizenInvocation<T> invocation) throws Throwable {
        String name = normalizeName(breakerName);
        Objects.requireNonNull(invocation, "invocation");

        KaizenConfig config = configRegistry.get(name);
        BreakerRuntimeContext context = stateRepository.getOrCreate(name, config);

        Instant beforeInvocation = clock.instant();
        context.beforeInvocation(beforeInvocation);

        try {
            T result = invocation.proceed();
            context.recordSuccess(clock.instant());
            return result;
        }
        catch (Throwable throwable) {
            context.recordFailure(clock.instant());
            throw throwable;
        }
    }

    private static String normalizeName(String breakerName) {
        String trimmed = Objects.requireNonNull(breakerName, "breakerName").trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("breakerName must not be blank");
        }
        return trimmed;
    }
}
