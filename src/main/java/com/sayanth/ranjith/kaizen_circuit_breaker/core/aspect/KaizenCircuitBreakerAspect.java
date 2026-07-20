package com.sayanth.ranjith.kaizen_circuit_breaker.core.aspect;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.annotation.KaizenCircuitBreaker;
import com.sayanth.ranjith.kaizen_circuit_breaker.core.engine.KaizenCircuitBreakerEngine;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Bridges the annotation to the breaker engine.
 */
@Aspect
@Component
public class KaizenCircuitBreakerAspect {

    private final KaizenCircuitBreakerEngine engine;

    public KaizenCircuitBreakerAspect(KaizenCircuitBreakerEngine engine) {
        this.engine = engine;
    }

    @Around("@annotation(kaizenCircuitBreaker)")
    public Object around(ProceedingJoinPoint joinPoint, KaizenCircuitBreaker kaizenCircuitBreaker) throws Throwable {
        String breakerName = resolveName(kaizenCircuitBreaker);
        return engine.execute(breakerName, joinPoint::proceed);
    }

    private static String resolveName(KaizenCircuitBreaker annotation) {
        String value = normalize(annotation.value());
        if (!value.isBlank()) {
            return value;
        }
        String name = normalize(annotation.name());
        if (!name.isBlank()) {
            return name;
        }
        throw new IllegalArgumentException("@KaizenCircuitBreaker requires a breaker name");
    }

    private static String normalize(String value) {
        return Objects.requireNonNullElse(value, "").trim();
    }
}
