package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.type.KaizenSlidingWindowType;
import com.sayanth.ranjith.kaizen_circuit_breaker.exception.KaizenConfigurationException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KaizenConfigTest {

    @Test
    void rejectsInvalidFailureThresholds() {
        KaizenConfigurationException exception = assertThrows(
                KaizenConfigurationException.class,
                () -> new KaizenConfig(
                        0,
                        10,
                        20,
                        KaizenSlidingWindowType.COUNT_BASED,
                        Duration.ofSeconds(30),
                        5
                )
        );

        assertEquals("failureRateThreshold must be between 1 and 100", exception.getMessage());
    }

    @Test
    void createsConfigFromProperty() {
        KaizenProperties.Breaker property = new KaizenProperties.Breaker();
        property.setName("inventory");
        property.setFailureRateThreshold(60);
        property.setMinimumNumberOfCalls(12);
        property.setSlidingWindowSize(20);
        property.setSlidingWindowType(KaizenSlidingWindowType.TIME_BASED);
        property.setWaitDurationInOpenState(Duration.ofSeconds(45));
        property.setPermittedCallsInHalfOpenState(4);

        KaizenConfig config = KaizenConfig.from(property);

        assertEquals(60, config.failureRateThreshold());
        assertEquals(12, config.minimumNumberOfCalls());
        assertEquals(20, config.slidingWindowSize());
        assertEquals(KaizenSlidingWindowType.TIME_BASED, config.slidingWindowType());
        assertEquals(Duration.ofSeconds(45), config.waitDurationInOpenState());
        assertEquals(4, config.permittedCallsInHalfOpenState());
    }
}
