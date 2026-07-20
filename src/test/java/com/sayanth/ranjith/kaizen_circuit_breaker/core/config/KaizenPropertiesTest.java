package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.type.KaizenSlidingWindowType;
import com.sayanth.ranjith.kaizen_circuit_breaker.exception.KaizenConfigurationException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KaizenPropertiesTest {

    @Test
    void registryConvertsNamedCircuitBreakersToConfigs() {
        KaizenProperties properties = new KaizenProperties();
        KaizenProperties.Breaker property = new KaizenProperties.Breaker();
        property.setName("inventory");
        property.setFailureRateThreshold(75);
        property.setMinimumNumberOfCalls(8);
        property.setSlidingWindowSize(16);
        property.setSlidingWindowType(KaizenSlidingWindowType.COUNT_BASED);
        property.setWaitDurationInOpenState(Duration.ofSeconds(20));
        property.setPermittedCallsInHalfOpenState(3);

        properties.setCircuitBreakers(List.of(property));

        KaizenConfig config = new DefaultKaizenConfigRegistry(properties).get("inventory");

        assertEquals(75, config.failureRateThreshold());
        assertEquals(8, config.minimumNumberOfCalls());
        assertEquals(16, config.slidingWindowSize());
        assertEquals(KaizenSlidingWindowType.COUNT_BASED, config.slidingWindowType());
        assertEquals(Duration.ofSeconds(20), config.waitDurationInOpenState());
        assertEquals(3, config.permittedCallsInHalfOpenState());
    }

    @Test
    void registryRejectsDuplicateBreakerNames() {
        KaizenProperties properties = new KaizenProperties();

        KaizenProperties.Breaker first = new KaizenProperties.Breaker();
        first.setName("inventory");

        KaizenProperties.Breaker second = new KaizenProperties.Breaker();
        second.setName("inventory");

        properties.setCircuitBreakers(List.of(first, second));

        assertThrows(
                KaizenConfigurationException.class,
                () -> new DefaultKaizenConfigRegistry(properties)
        );
    }
}
