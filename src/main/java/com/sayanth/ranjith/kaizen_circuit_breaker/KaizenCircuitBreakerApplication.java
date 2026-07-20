package com.sayanth.ranjith.kaizen_circuit_breaker;

import java.time.Clock;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Application entry point for the Kaizen Circuit Breaker service.
 * <p>
 * This class bootstraps the Spring Boot application context and starts the
 * embedded server.
 * </p>
 *
 * @author Sayanth P V
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class KaizenCircuitBreakerApplication {

	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

	/**
	 * Starts the application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(KaizenCircuitBreakerApplication.class, args);
	}

}
