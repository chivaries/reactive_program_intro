package com.glamrock.lecture1.reactive_streams;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
public class TestSubscriber implements Subscriber<Integer> {
    private Integer count;
    private final Integer DEMAND_COUNT = 3;
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("subscriber:onSubscribe");

        count = DEMAND_COUNT;
        this.subscription = subscription;
        this.subscription.request(DEMAND_COUNT);
    }

    @Override
    public void onNext(Integer integer) {
        log.info("subscriber:onNext");

        synchronized (this) {
            count--;
            if(count == 0) {
              log.info("count is zero");
              count = DEMAND_COUNT;
              subscription.request(count);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("subscriber:onError");
    }

    @Override
    public void onComplete() {
        log.info("subscriber:onComplete");
    }
}
