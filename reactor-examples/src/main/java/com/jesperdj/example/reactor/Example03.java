package com.jesperdj.example.reactor;

import reactor.core.publisher.Flux;

/**
 * Example03: Infinite flux and using zipWith.
 */
public class Example03 {

    public static void main(String[] args) {
        Flux<String> words = Flux.just("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog");

        // Infinite flux of numbers
        Flux<Integer> numbers = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next(state);
                    return state + 1;
                });

        // Combine fluxes using zipWith
        numbers.zipWith(words)
                .map(tuple -> String.format("%d: %s", tuple.getT1(), tuple.getT2()))
                .subscribe(System.out::println);
    }
}
