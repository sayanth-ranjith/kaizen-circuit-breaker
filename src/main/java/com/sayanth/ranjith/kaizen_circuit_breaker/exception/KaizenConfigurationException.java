package com.sayanth.ranjith.kaizen_circuit_breaker.exception;

/**
 * Signals that a circuit breaker configuration is invalid or cannot be resolved.
 */
public class KaizenConfigurationException extends RuntimeException {

    public KaizenConfigurationException(String message) {
        super(message);
    }

    public KaizenConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
