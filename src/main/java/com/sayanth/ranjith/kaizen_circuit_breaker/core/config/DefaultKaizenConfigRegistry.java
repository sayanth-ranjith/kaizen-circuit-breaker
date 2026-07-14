package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default in-memory registry for breaker configs.
 */
@Component
public class DefaultKaizenConfigRegistry implements KaizenConfigRegistry {

    private final Map<String, KaizenConfig> configs;

    public DefaultKaizenConfigRegistry(KaizenProperties properties) {
        this.configs = buildConfigs(properties.getCircuitBreakers());
    }

    @Override
    public KaizenConfig get(String name) {
        KaizenConfig config = configs.get(name);
        if (config == null) {
            throw new KaizenConfigException("No Kaizen config found for '" + name + "'");
        }
        return config;
    }

    @Override
    public Map<String, KaizenConfig> getAll() {
        return configs;
    }

    private static Map<String, KaizenConfig> buildConfigs(List<KaizenProperties.Breaker> breakers) {
        Map<String, KaizenConfig> resolved = new LinkedHashMap<>();
        for (KaizenProperties.Breaker breaker : breakers) {
            if (breaker == null) {
                throw new KaizenConfigException("circuitBreakers must not contain null entries");
            }
            String name = normalizeName(breaker.getName());
            KaizenConfig config = KaizenConfig.from(breaker);
            KaizenConfig previous = resolved.putIfAbsent(name, config);
            if (previous != null) {
                throw new KaizenConfigException("Duplicate Kaizen config name '" + name + "'");
            }
        }
        return Map.copyOf(resolved);
    }

    private static String normalizeName(String name) {
        String trimmed = Objects.requireNonNull(name, "name").trim();
        if (trimmed.isEmpty()) {
            throw new KaizenConfigException("Breaker name must not be blank");
        }
        return trimmed;
    }
}
