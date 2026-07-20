package com.sayanth.ranjith.kaizen_circuit_breaker.core.aspect;

import com.sayanth.ranjith.kaizen_circuit_breaker.exception.KaizenCircuitBreakerOpenException;
import com.sayanth.ranjith.kaizen_circuit_breaker.support.AnnotatedDemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "kaizen.circuit-breakers[0].name=inventory",
        "kaizen.circuit-breakers[0].failure-rate-threshold=50",
        "kaizen.circuit-breakers[0].minimum-number-of-calls=1",
        "kaizen.circuit-breakers[0].sliding-window-size=1",
        "kaizen.circuit-breakers[0].sliding-window-type=COUNT_BASED",
        "kaizen.circuit-breakers[0].wait-duration-in-open-state=1h",
        "kaizen.circuit-breakers[0].permitted-calls-in-half-open-state=1"
})
class KaizenCircuitBreakerAspectIntegrationTest {

    @Autowired
    private AnnotatedDemoService service;

    @Test
    void annotationExecutesBusinessCodeUntilBreakerOpens() {
        assertEquals("ok", service.execute(false));

        assertThrows(IllegalStateException.class, () -> service.execute(true));
        assertEquals(2, service.invocationCount());

        assertThrows(
                KaizenCircuitBreakerOpenException.class,
                () -> service.execute(false)
        );
        assertEquals(2, service.invocationCount());
    }
}
