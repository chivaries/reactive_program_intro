package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class L7_RequestTests {
    @Test
    public void backPressureTest() throws InterruptedException {
        // 16:11:33.558 [main] INFO reactor.Flux.Take.1 - request(unbounded) -> 배압을 unbounded 로 알아서 설정
        Flux<Long> flux = Flux.interval(Duration.ofMillis(100)).take(4).log();
        flux.doOnNext(v -> {
            System.out.println(v);
            System.out.println(Thread.currentThread().getName());
        }).subscribe();

        // sleep 을 주지 않으면 아무것도 표시 안되고 끝남
        // 데몬쓰레드를 띄워서 실행시키는데 유저 쓰레드(main thread)가 하나도 없을 경우 그냥 죽음
        // 유저 쓰레드를 sleep 으로 살려둘 경우에는 실행됨
        Thread.sleep(1000l);
    }

    @Test
    public void requestTest() throws InterruptedException {
        StepVerifier.create(Flux.just("user1", "user2", "user3").log(), 1)
                .expectNext("user1")
                .thenRequest(1)
                .expectNext("user2")
                .thenRequest(1)
                .expectNext("user3")
                .expectComplete()
                .verify();

        // 실행 중  cancel 하고 verify
        // --> thenCancel() 실행 후 vefify
        StepVerifier.create(Flux.just("user1", "user2", "user3").log(), 1)
                .expectNext("user1")
                .thenRequest(1)
                .expectNext("user2")
                .thenCancel()
                .verify();

        // 2개만 take 하고 verify
        // take(2) 가 upstream 에 2개가 지나면 cancel 을 날리고 Flux 를 새로 만듦
        // --> vefifyComplete() 가 가능
        StepVerifier.create(Flux.just("user1", "user2", "user3").take(2).log(), 1)
                .expectNext("user1")
                .thenRequest(1)
                .expectNext("user2")
                .verifyComplete();
    }

    @Test
    public void requestTest2() throws InterruptedException {
        Flux.just("user1", "user2", "user3").log()
                .doOnSubscribe(s -> System.out.println("Starting"))
                .doOnNext(u -> System.out.println(u))
                .doOnComplete(() -> System.out.println("The end!"))
                .subscribe();

        //Thread.sleep(1000L);
    }
}
