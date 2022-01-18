package com.glamrock.lecture1.reactive_streams;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestSubscription implements Subscription {
    private final Subscriber<? super Integer> subscriber;
    private final ExecutorService executorService;
    private final AtomicInteger value;

    public TestSubscription(Subscriber<? super Integer> subscriber, ExecutorService executorService) {
        this.subscriber = subscriber;
        this.executorService = executorService;
        this.value = new AtomicInteger();
    }

    @Override
    public void request(long n) {
        if (n < 0) { // TODO:error
        } else {
            for(int i = 0; i < n; i++) {
                int count = value.incrementAndGet();

                if (count > 1000) {
                    log.info("Item is over");
                    subscriber.onComplete();
                } else {
                    log.info("push Item + " + count);
                    subscriber.onNext(count);
                }
            }
        }
    }

    @Override
    public void cancel() {
        subscriber.onComplete();
    }
}
