package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class L8_BackPressureTests {
    @Test
    public void backPressureTest() {
        // 16:47:34.696 [main] INFO reactor.Flux.Array.1 - | request(unbounded) -> unbounded 로 받음
//        Flux.range(1, 100)
//                .log()
//                .doOnNext(System.out::println)
//                .subscribe();

        // backPressure 적용
        // 17:01:58.795 [main] INFO reactor.Flux.Range.1 - | request(10) -> 10 개씩 받음
        // Spring webFlux 를 사용하면 Flux 를 리턴할 경우 알아서 subscribe 하면서 request 를 조정한다. (31 로 고정됨)
        Flux.range(1, 100)
                .log()
                .doOnNext(System.out::println)
                .subscribe(new Subscriber<Integer>() {
                    private Subscription subscription;
                    private int count;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        this.subscription.request(10);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        count++;
                        if (count % 10 == 0) {
                            this.subscription.request(10);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
