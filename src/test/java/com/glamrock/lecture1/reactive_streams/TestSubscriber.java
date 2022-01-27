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

    /*
    데이터를 전달 받았을 때의 로직을 구현
     */
    @Override
    public void onNext(Integer integer) {
        log.info("subscriber:onNext");

        synchronized (this) {
            count--;
            if(count == 0) {
              log.info("count is zero");
              count = DEMAND_COUNT;
              /*
              어떻게 데이터를 전송하는지는 Subscription 에 정의
              request 메소드에서 Subscriber 로 데이터를 전달하기 위해 onNext 를 실행한다.
              count 를 backpressure 로 사용
               */
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
