package com.jesperdj.example.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * Example06: Schedulers.
 */
public class Example06 {
    private static final Logger LOG = LoggerFactory.getLogger(Example06.class);

    public static void main(String[] args) throws InterruptedException {
        Flux<Long> ticks = Flux.interval(Duration.ofMillis(500));

        Scheduler scheduler = Schedulers.newSingle("tick", true);

        ticks.publishOn(scheduler)
                .subscribe(n -> LOG.info("Tick {}", n));

        Thread.sleep(5000L);
    }
}
