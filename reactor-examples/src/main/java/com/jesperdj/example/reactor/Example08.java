package com.jesperdj.example.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Example08: Calling two webservices with a timeout; error handling.
 */
public class Example08 {
    private static final Logger LOG = LoggerFactory.getLogger(Example08.class);

    private static Mono<FakeHttpResponse> callWebservice(FakeHttpRequest request) {
        Random random = new Random();
        int latency = random.nextInt(1900) + 100;

        return Mono.just(request)
                .doOnNext(req -> LOG.info("Webservice called: {}", req.getUrl()))
                .delayElement(Duration.ofMillis(latency))
                .map(req -> new FakeHttpResponse(req.getUrl(), 200));
    }

    public static void main(String[] args) throws InterruptedException {
        LOG.info("Preparing webservice calls");

        Mono<FakeHttpResponse> mono1 = callWebservice(new FakeHttpRequest("http://one.com"))
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(TimeoutException.class, exception -> {
                    LOG.warn("First webservice timed out");
                    return Mono.empty();
                });

        Mono<FakeHttpResponse> mono2 = callWebservice(new FakeHttpRequest("http://two.com"))
                .timeout(Duration.ofSeconds(1));

        Mono<FakeHttpResponse> responseMono = mono1.switchIfEmpty(mono2);

        LOG.info("Going to subscribe");
        responseMono.subscribe(
                response -> LOG.info("Response: {}", response),
                error -> LOG.error("Error: {}", error.getMessage()),
                () -> LOG.info("Complete")
        );

        Thread.sleep(5000L);
    }
}
