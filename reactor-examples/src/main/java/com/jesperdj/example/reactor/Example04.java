package com.jesperdj.example.reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Example04: Flux of timer ticks.
 */
public class Example04 {

    public static void main(String[] args) throws InterruptedException {
        // Flux that produces an item every second
        Flux<Long> ticks = Flux.interval(Duration.ofSeconds(1));

        ticks.take(10)
                .subscribe(System.out::println);

        Thread.sleep(11000L);
    }
}
