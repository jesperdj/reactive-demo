package com.jesperdj.example.reactor;

import reactor.core.publisher.Flux;

/**
 * Example01: Creating a Flux and subscribing to it.
 */
public class Example01 {

    public static void main(String[] args) {
        // Create a Flux from a list of strings
        Flux<String> words = Flux.just("The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog");

        // Subscribe to let it publish the strings
        words.subscribe(System.out::println);
    }
}
