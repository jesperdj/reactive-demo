package com.jesperdj.example.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * TestExample02: Using StepVerifier with virtual time.
 */
public class TestExample02 {

    @Test
    public void stepVerifierWithVirtualTime() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofMinutes(1)).take(60))
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(59))
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(60)
                .verifyComplete();
    }
}
