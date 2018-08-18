package com.jesperdj.example.reactor;

import reactor.core.publisher.Flux;

/**
 * Example02: Applying operators.
 */
public class Example02 {

    public static void main(String[] args) {
        Flux<String> words = Flux.just("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog");

        // Apply operators
        words.map(String::toUpperCase)
                .filter(word -> word.length() <= 4)
                .distinct()
                .sort()
                .subscribe(System.out::println);
    }
}
