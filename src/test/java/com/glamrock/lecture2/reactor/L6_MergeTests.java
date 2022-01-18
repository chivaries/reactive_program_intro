package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class L6_MergeTests {
    @Test
    void mergeTest() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100)).take(10);
        Flux<Long> flux2 = Flux.just(100l, 101l, 102l);

        flux1.mergeWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();

    }

    @Test
    void concatWithTest() {
        Flux<Long> flux1 = Flux.interval(Duration.ofMillis(100)).take(10);
        Flux<Long> flux2 = Flux.just(100l, 101l, 102l);

        flux1.concatWith(flux2)
                .doOnNext(System.out::println)
                .blockLast();

    }

    @Test
    void concatTest() {
        Mono<Integer> mono1 = Mono.just(1);
        Mono<Integer> mono2 = Mono.just(2);
        Mono<Integer> mono3 = Mono.just(3);

        Flux.concat(mono1, mono2, mono3)
                .doOnNext(System.out::println)
                .blockLast();
    }
}
