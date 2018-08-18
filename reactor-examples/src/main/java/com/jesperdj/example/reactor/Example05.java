package com.jesperdj.example.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Example05: Asynchronocity - subscriber is called on a different thread.
 */
public class Example05 {
    private static final Logger LOG = LoggerFactory.getLogger(Example05.class);

    public static void main(String[] args) throws InterruptedException {
        Flux<Long> ticks = Flux.interval(Duration.ofSeconds(1));

        // Fluxes are asynchronous: see that the subscriber is called on a different thread
        ticks.subscribe(n -> LOG.info("{}", n));

        Thread.sleep(11000L);
    }
}
