package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.type.KaizenSlidingWindowType;

import java.time.Duration;
import java.util.Objects;

public record KaizenConfig(int failureRateThreshold,
                           int minimumNumberOfCalls,
                           int slidingWindowSize,
                           KaizenSlidingWindowType slidingWindowType,
                           Duration waitDurationInOpenState,
                           int permittedCallsInHalfOpenState) {
    public KaizenConfig {
        if (failureRateThreshold < 1 || failureRateThreshold > 100) {
            throw new IllegalArgumentException(
                    "failureRateThreshold must be between 1 and 100"
            );
        }
        if (minimumNumberOfCalls < 1) {
            throw new IllegalArgumentException(
                    "minimumNumberOfCalls must be greater than zero"
            );
        }
        if (slidingWindowSize < 1) {
            throw new IllegalArgumentException(
                    "slidingWindowSize must be greater than zero"
            );
        }
        if (minimumNumberOfCalls > slidingWindowSize) {
            throw new IllegalArgumentException(
                    "minimumNumberOfCalls cannot exceed slidingWindowSize"
            );
        }
        Objects.requireNonNull(
                slidingWindowType,
                "slidingWindowType must not be null"
        );
        Objects.requireNonNull(
                waitDurationInOpenState,
                "waitDurationInOpenState must not be null"
        );
        if (waitDurationInOpenState.isZero()
                || waitDurationInOpenState.isNegative()) {
            throw new IllegalArgumentException(
                    "waitDurationInOpenState must be positive"
            );
        }
        if (permittedCallsInHalfOpenState < 1) {
            throw new IllegalArgumentException(
                    "permittedCallsInHalfOpenState must be greater than zero"
            );
        }
    }
}
