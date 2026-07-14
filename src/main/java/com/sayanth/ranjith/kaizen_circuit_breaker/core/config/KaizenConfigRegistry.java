package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

import java.util.Map;

/**
 * Resolves named breaker definitions into immutable runtime configs.
 */
public interface KaizenConfigRegistry {

    KaizenConfig get(String name);

    Map<String, KaizenConfig> getAll();
}
