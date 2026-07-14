package com.sayanth.ranjith.kaizen_circuit_breaker.core.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Raw configuration properties for named circuit breakers.
 * <p>
 * This class only binds external configuration. The runtime domain conversion is
 * handled by {@link DefaultKaizenConfigRegistry}.
 * </p>
 */
@ConfigurationProperties(prefix = "kaizen")
@Validated
public class KaizenProperties {

    @Valid
    private List<Breaker> circuitBreakers = new ArrayList<>();

    public List<Breaker> getCircuitBreakers() {
        return List.copyOf(circuitBreakers);
    }

    public void setCircuitBreakers(List<Breaker> circuitBreakers) {
        this.circuitBreakers = circuitBreakers == null
                ? new ArrayList<>()
                : new ArrayList<>(circuitBreakers);
    }

    @Getter
    @Setter
    public static class Breaker {
        @NotBlank
        private String name;

        @Min(1)
        @Max(100)
        private int failureRateThreshold = 50;

        @Min(1)
        private int minimumNumberOfCalls = 10;

        @Min(1)
        private int slidingWindowSize = 20;

        @NotNull
        private com.sayanth.ranjith.kaizen_circuit_breaker.core.type.KaizenSlidingWindowType slidingWindowType =
                com.sayanth.ranjith.kaizen_circuit_breaker.core.type.KaizenSlidingWindowType.COUNT_BASED;

        @NotNull
        private Duration waitDurationInOpenState = Duration.ofSeconds(30);

        @Min(1)
        private int permittedCallsInHalfOpenState = 5;
    }
}
