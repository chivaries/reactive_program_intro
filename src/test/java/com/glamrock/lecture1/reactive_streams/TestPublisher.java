package com.glamrock.lecture1.reactive_streams;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TestPublisher implements Publisher<Integer> {
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        log.info("Publisher:subscribe");
        subscriber.onSubscribe(new TestSubscription(subscriber, executor));
    }
}
