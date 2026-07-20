package com.sayanth.ranjith.kaizen_circuit_breaker.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as protected by a named Kaizen circuit breaker.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KaizenCircuitBreaker {

    /**
     * Breaker name from configuration.
     */
    String value() default "";

    /**
     * Alias for {@link #value()} when a named attribute is preferred.
     */
    String name() default "";
}
