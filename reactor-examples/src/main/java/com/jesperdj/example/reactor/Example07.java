package com.jesperdj.example.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

/**
 * Example07: Calling two webservices and taking the result of the fastest one.
 */
public class Example07 {
    private static final Logger LOG = LoggerFactory.getLogger(Example07.class);

    private static Mono<FakeHttpResponse> callWebservice(FakeHttpRequest request) {
        Random random = new Random();
        int latency = random.nextInt(900) + 100;

        return Mono.just(request)
                .doOnNext(req -> LOG.info("Webservice called: {}", req.getUrl()))
                .delayElement(Duration.ofMillis(latency))
                .map(req -> new FakeHttpResponse(req.getUrl(), 200));
    }

    public static void main(String[] args) throws InterruptedException {
        LOG.info("Preparing webservice calls");
        Mono<FakeHttpResponse> mono1 = callWebservice(new FakeHttpRequest("http://one.com"));
        Mono<FakeHttpResponse> mono2 = callWebservice(new FakeHttpRequest("http://two.com"));

        Mono<FakeHttpResponse> responseMono = Mono.first(mono1, mono2);

        LOG.info("Going to subscribe");
        responseMono.subscribe(response -> LOG.info("Response: {}", response));

        Thread.sleep(2000L);
    }
}
