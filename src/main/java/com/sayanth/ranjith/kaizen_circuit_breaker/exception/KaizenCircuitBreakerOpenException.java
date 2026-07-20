package com.sayanth.ranjith.kaizen_circuit_breaker.exception;

/**
 * Raised when a protected call is rejected because the breaker is open.
 */
public class KaizenCircuitBreakerOpenException extends RuntimeException {

    public KaizenCircuitBreakerOpenException(String message) {
        super(message);
    }
}
