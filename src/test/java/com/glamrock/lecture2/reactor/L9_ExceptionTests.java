package com.glamrock.lecture2.reactor;


import org.junit.jupiter.api.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

public class L9_ExceptionTests {
    @Test
    public void ExcepitonTest() {
//        Mono<Object> mono = Mono.error(new RuntimeException());
//
//        mono.log().onErrorReturn(Mono.just(2)).doOnNext(System.out::println).subscribe();
//
//        mono.log().onErrorResume(e -> Mono.just(2)).doOnNext(System.out::println).subscribe();

        Mono.just("hello")
                .log()
                .map(s -> {
                    int value = 0;
                    try {
                      return Integer.parseInt(s);
                    } catch (Exception e) {
                        //Exceptions#propagate utility will wrap a checked exception into a special runtime exception that can be automatically unwrapped by Reactor subscribers
                        throw Exceptions.propagate(e);
                    }
                })
                .onErrorReturn(200)
                .doOnNext(System.out::println)
                .subscribe();
    }

    public static class MyException extends Exception {}
}
