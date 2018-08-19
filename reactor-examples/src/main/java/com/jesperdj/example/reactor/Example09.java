package com.jesperdj.example.reactor;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.Random;

/**
 * Example09: Push vs pull and backpressure.
 */
public class Example09 {
    private static final Logger LOG = LoggerFactory.getLogger(Example09.class);

    public static void main(String[] args) {
        Flux<Integer> randomNumbers = Flux.generate(
                Random::new,
                (random, sink) -> {
                    sink.next(random.nextInt(1000));
                    return random;
                });

        randomNumbers.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(20);
            }

            @Override
            protected void hookOnNext(Integer value) {
                LOG.info("{}", value);
            }

            @Override
            protected void hookOnComplete() {
                LOG.info("Complete");
            }
        });
    }
}
