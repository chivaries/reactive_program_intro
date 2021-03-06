package com.glamrock.lecture2.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//@SpringBootTest
@Slf4j
class L1_FluxBasicTests {

    @Test
    void contextLoads() {
    }

    @Test
    void fluxCreate() throws InterruptedException {
        /*
            Publisher (발행자)는 구독이 되었을 경우에만 데이터를 Subscriber(구독자)에게 전달한다.
            Subscriber(구독자)가 Publisher 에 구독을 하는 과정은, Publisher(발행자)에 정의된 subscribe() 메서드를 사용한다.
            이때, 매개변수로 Consumer 함수를 전달할 수 있는데,
            Consumer 함수는 데이터 전달을 해서 Subscriber의 onNext 이벤트가 발생을 했을때 실행되는 함수이다

            이벤트는 아래의 순서로 실행 될 것이다.
            onSubscribe --> request --> onNext --> onNext --> onComplete

             subscribe() 를 실행하면서 매개변수가 Consumer 함수 하나인 메서드를 실행하였다.
             이런 경우에는, request(unbounded) 가 실행 된다. (PubSubTests.class 에서 Publisher 동작원리 참조)
             unbounded 로 전달되면, 내부적으로 requext(MAX)로 적용이 된다. 즉, 모든 데이터를 전달하라고 요청하는 것이다.
         */
        Flux.fromIterable(Arrays.asList("foo", "bar"))
                .doOnNext(System.out::println)
                .map(String::toUpperCase).log()
                .subscribe(System.out::println);

        System.out.println("이게 먼저 찍히나??");
    }

    @Test
    void fluxCreateAnotherFlux() {
        Flux<String> flux = Flux.just("A");
        flux.map(i -> "foo" + i);
        // "fooA" 가 아니고 "A" 가 나온다.
        // flux.map 에서 또 다른 publisher 가 생성됨.
        flux.subscribe(System.out::println);

        flux.map(i -> "foo" + i).subscribe(System.out::println);

        Flux<String> flux2 = flux.map(i -> "foo" + i);
        flux2.subscribe(System.out::println);
    }

    @Test
    void fluxAsync() throws InterruptedException {
        Flux.interval(Duration.ofMillis(100))
                .take(10).log()
                .subscribe(System.out::println);

        System.out.println("이게 먼저 찍히나??");

        // 아래 주석처리 하면 숫자 안찍힘힘
       Thread.sleep(2000);
    }

    @Test
    void fluxLazy(){
        Flux<Integer> flux = Flux.range(1,9)
                .map(n -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 3 * n;
                }).log();
/*
        Flux<Integer> flux = Flux.range(1,9)
                .flatMap(n -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return Mono.just(3 * n);
                });
 */
        System.out.println("아직 구독 안함... 데이터 전달 안됨");

        flux.subscribe(value -> {
                        System.out.println(value);
                        },
                        null,
                        () -> {
                            System.out.println("데이터 수신 완료");
                        });

        System.out.println("전체 완료");
    }

    @Test
    void fluxBackPressure(){
        Flux<Integer> flux = Flux.range(1,9)
                .map(n -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 3 * n;
                }).log();

        System.out.println("아직 구독 안함... 데이터 전달 안됨");

        flux.subscribe(value -> {
                    System.out.println(value);
                },
                null,
                () -> {
                    System.out.println("데이터 수신 완료");
                },
                subscription -> subscription.request(1)
        );

        System.out.println("전체 완료");
    }
}
