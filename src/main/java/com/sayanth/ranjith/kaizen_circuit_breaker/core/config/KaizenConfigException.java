package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

/**
 * Signals that a circuit breaker configuration is invalid or cannot be resolved.
 */
public class KaizenConfigException extends RuntimeException {

    public KaizenConfigException(String message) {
        super(message);
    }

    public KaizenConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
