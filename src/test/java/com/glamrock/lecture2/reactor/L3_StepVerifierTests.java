package com.glamrock.lecture2.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class L3_StepVerifierTests {

    @Test
    void expectNextTest() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete();
    }

    @Test
    void errorTest() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyError(RuntimeException.class);
    }

    @Test
    void assertNextTest() {
        Flux<User> users = Flux.just(new User("swhite"), new User("jpinkman"));
        StepVerifier.create(users)
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("swhitte"))
                .assertNext(u -> assertThat(u.getUsername()).isEqualTo("jpinkman"))
                .verifyComplete();
    }

    @Test
    void intervalTest() {
        Flux<Long> take10 = Flux.interval(Duration.ofMillis(100))
                .take(10);

        StepVerifier.create(take10)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void virtualTest() {
        // Expect 3600 elements at intervals of 1 second, and verify quicker than 3600s
        StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofHours(3)))
                .thenAwait(Duration.ofHours(1))
                .expectNextCount(3600)
                .verifyComplete();
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
}
