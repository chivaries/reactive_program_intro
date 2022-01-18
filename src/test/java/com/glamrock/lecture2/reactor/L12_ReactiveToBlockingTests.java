package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class L12_ReactiveToBlockingTests {
    @Test
    public void reactiveToBlockingTest() {
        // Mono blocking
        Mono<Integer> mono1 = Mono.just(1);
        Integer resultInt = mono1.block();

        // Flux blocking
        Flux<Integer> flux = Flux.range(0, 10);
        flux.blockFirst();
        flux.blockLast();

        // Flux -> Iterable
        Iterable<Integer> integerIterable = Flux.range(0, 10).toIterable();
    }

    @Test
    public void blockingToReactiveTest1() throws InterruptedException {
        UserRepository repository = new UserRepository();
        // blocking 되는 코드만 subscribeOn 을 이용해서 Schedulers.elastic() 의 thread 로 실행
        //
        Flux.defer(() -> Flux.fromIterable(repository.findAll()))
                .doOnNext(u -> {
                    System.out.println(u);
                    System.out.println(Thread.currentThread().getName());
                })
                .subscribeOn(Schedulers.elastic())
                .subscribe();

        Thread.sleep(4000);
    }

    @Test
    public void blockingToReactiveTest2() throws InterruptedException {
        UserRepository repository = new UserRepository();
        Flux<User> flux = Flux.just(new User("tom"), new User("jane"));
        flux.publishOn(Schedulers.elastic())
                .doOnNext(repository::save)
                .then();

        Thread.sleep(4000);
    }

    public static class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getUser() {
            return "User{" +
                    "name='" + name + "'" +
                    "}";
        }

    }

    public static class UserRepository {
        public List<User> findAll() {
            return List.of(new User("tom"), new User("jane"));
        }

        public void save(User user) {
        }
    }
}
