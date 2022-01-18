package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static reactor.core.scheduler.Schedulers.parallel;

public class L4_TransformTests {
    @Test
    void mapTest() {
        // map 을 사용하면 동기적으로 변환됨
        StepVerifier.create(Mono.just(new User("hello"))
                .map(u -> new User(u.getUsername().toUpperCase())))
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("HELLO"))
                .verifyComplete();
    }

    @Test
    void flatMapTest() {
        // 변환식을 Mono 를 리턴하게 만들고 flatMap 을 사용하면 비동기적으로 변환됨
        StepVerifier.create(Mono.just(new User("hello"))
                .flatMap(this::asyncCapitalizeUser))
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("HELLO"))
                .verifyComplete();
    }

    @Test
    void flatMapTest2() {
        // flatMap 으로 변환했지만 병렬 실행이 되지 않는다.
        Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i")
                .window(3)
                .flatMap(l -> l.map(this::toUpperCase))
                .doOnNext(System.out::println)
                .blockLast();

        // 3개(window(3)) 가 비동기 실행됨
        Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i")
                .window(3)
                .flatMap(l -> l.map(this::toUpperCase).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();

        // 순차적으로 실행 but) 비동기 실행이 안됨
        Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i")
                .window(3)
                .concatMap(l -> l.map(this::toUpperCase).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();

        // 순차적으로 실행되면서 내부적으로 비동기 실행행
       Flux.just("a", "b", "c", "d", "e", "f", "g", "h", "i")
                .window(3)
                .flatMapSequential(l -> l.map(this::toUpperCase).subscribeOn(parallel()))
                .doOnNext(System.out::println)
                .blockLast();
    }

    Mono<User> asyncCapitalizeUser(User u) {
        return Mono.just(new User(u.getUsername().toUpperCase()));
    }

    public static class User {
        private String username;

        public User(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    private List<String> toUpperCase(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s.toUpperCase(), Thread.currentThread().getName());
    }
}
