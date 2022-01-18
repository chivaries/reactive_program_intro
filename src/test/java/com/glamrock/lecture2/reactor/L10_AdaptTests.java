package com.glamrock.lecture2.reactor;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class L10_AdaptTests {
    @Test
    public void adaptTest() throws ExecutionException, InterruptedException {
        // flux -> flowable
        var flowable = Flowable.fromPublisher(Flux.just(2));

        // Flowable -> Flux
        Flux.from(flowable);

        // flux -> observable
        Flux<Integer> flux = Flux.just(2);
        Observable<Integer> observable = Observable.just(2);

        // Observable -> Flux
        // Observable 은 Publisher 를 구현하지 않아서 아래와 같이 못쓴다.
        //flux.from(observable)
        // Observable 을 Flowable 로 바꾼 뒤 변환
        flux.from(observable.toFlowable(BackpressureStrategy.BUFFER));

        // Flux -> Observable
        Observable.fromPublisher(Flux.just(2));

        Mono<Integer> mono = Mono.just(2);
        Single<Integer> single = Single.just(2);

        // Mono -> Single
        // Single 은 Publisher 를 구현 안함
        Mono.from(single.toFlowable());

        // Single -> Mono
        Single.fromPublisher(mono);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello");
        future.thenApply(s -> s.toUpperCase());

        Mono<String> mono2 = Mono.just("hello").map(s -> s.toUpperCase());

        // Future -> Mono
        Mono.fromFuture(future);

        // Mono -> Future
        mono.toFuture();
    }
}
