package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.type.KaizenSlidingWindowType;

import java.time.Duration;

public record KaizenConfig(int failureRateThreshold,
                           int minimumNumberOfCalls,
                           int slidingWindowSize,
                           KaizenSlidingWindowType slidingWindowType,
                           Duration waitDurationInOpenState,
                           int permittedCallsInHalfOpenState) {
    public KaizenConfig {
        if (failureRateThreshold < 1 || failureRateThreshold > 100) {
            throw new KaizenConfigException(
                    "failureRateThreshold must be between 1 and 100"
            );
        }
        if (minimumNumberOfCalls < 1) {
            throw new KaizenConfigException(
                    "minimumNumberOfCalls must be greater than zero"
            );
        }
        if (slidingWindowSize < 1) {
            throw new KaizenConfigException(
                    "slidingWindowSize must be greater than zero"
            );
        }
        if (minimumNumberOfCalls > slidingWindowSize) {
            throw new KaizenConfigException(
                    "minimumNumberOfCalls cannot exceed slidingWindowSize"
            );
        }
        if (slidingWindowType == null) {
            throw new KaizenConfigException("slidingWindowType must not be null");
        }
        if (waitDurationInOpenState == null) {
            throw new KaizenConfigException("waitDurationInOpenState must not be null");
        }
        if (waitDurationInOpenState.isZero()
                || waitDurationInOpenState.isNegative()) {
            throw new KaizenConfigException(
                    "waitDurationInOpenState must be positive"
            );
        }
        if (permittedCallsInHalfOpenState < 1) {
            throw new KaizenConfigException(
                    "permittedCallsInHalfOpenState must be greater than zero"
            );
        }
    }

    public static KaizenConfig from(KaizenProperties.Breaker property) {
        if (property == null) {
            throw new KaizenConfigException("property must not be null");
        }
        return new KaizenConfig(
                property.getFailureRateThreshold(),
                property.getMinimumNumberOfCalls(),
                property.getSlidingWindowSize(),
                property.getSlidingWindowType(),
                property.getWaitDurationInOpenState(),
                property.getPermittedCallsInHalfOpenState()
        );
    }
}
