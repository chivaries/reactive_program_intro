package com.glamrock.lecture1.reactive_streams;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class PubSubTests {
    @Test
    public void pubSubTests() throws InterruptedException {
        Publisher<Integer> testPublisher = new TestPublisher();
        Subscriber<Integer> testSubscriber = new TestSubscriber();
        testPublisher.subscribe(testSubscriber);
        Thread.sleep(10000);
    }
}
