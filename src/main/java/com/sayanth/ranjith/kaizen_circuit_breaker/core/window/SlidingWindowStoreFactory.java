package com.sayanth.ranjith.kaizen_circuit_breaker.core.window;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.config.KaizenConfig;

/**
 * Creates the correct sliding window implementation for a breaker.
 */
public interface SlidingWindowStoreFactory {

    SlidingWindowStore create(KaizenConfig config);
}
