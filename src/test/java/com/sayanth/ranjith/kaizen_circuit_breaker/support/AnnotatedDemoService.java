package com.sayanth.ranjith.kaizen_circuit_breaker.support;

import com.sayanth.ranjith.kaizen_circuit_breaker.core.annotation.KaizenCircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AnnotatedDemoService {

    private final AtomicInteger invocationCount = new AtomicInteger();

    @KaizenCircuitBreaker("inventory")
    public String execute(boolean fail) {
        invocationCount.incrementAndGet();
        if (fail) {
            throw new IllegalStateException("downstream failed");
        }
        return "ok";
    }

    public int invocationCount() {
        return invocationCount.get();
    }
}
