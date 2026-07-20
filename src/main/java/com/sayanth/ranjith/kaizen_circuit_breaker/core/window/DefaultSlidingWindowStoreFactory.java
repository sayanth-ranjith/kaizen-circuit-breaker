package com.sayanth.ranjith.kaizen_circuit_breaker.core.window;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.config.KaizenConfig;
import org.springframework.stereotype.Component;

/**
 * Selects the right window implementation from the breaker config.
 */
@Component
public class DefaultSlidingWindowStoreFactory implements SlidingWindowStoreFactory {

    @Override
    public SlidingWindowStore create(KaizenConfig config) {
        return switch (config.slidingWindowType()) {
            case COUNT_BASED -> new CountBasedSlidingWindowStore(config.slidingWindowSize());
            case TIME_BASED -> new TimeBasedSlidingWindowStore(config.waitDurationInOpenState());
        };
    }
}
