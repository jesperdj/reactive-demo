package com.jesperdj.example.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * TestExample01: Using StepVerifier.
 */
public class TestExample01 {

    @Test
    public void stepVerifierExample1() {
        Flux<String> words = Flux.just("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog");

        StepVerifier.create(words)
                .expectNext("The")
                .expectNext("quick")
                .expectNext("brown")
                .expectNext("fox")
                .expectNext("jumps")
                .expectNext("over")
                .expectNext("the")
                .expectNext("lazy")
                .expectNext("dog")
                .verifyComplete();
    }

    @Test
    public void stepVerifierExample2() {
        Flux<String> words = Flux.just("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog");

        StepVerifier.create(words)
                .expectNext("The")
                .expectNext("quick")
                .expectNext("brown")
                .expectNext("fox")
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void stepVerifierExample3() {
        Flux<String> words = Flux.just("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog")
                .concatWith(Mono.error(new IllegalStateException("exception")));

        StepVerifier.create(words)
                .expectNextCount(9)
                .expectError(IllegalStateException.class)
                .verify();
    }
}
