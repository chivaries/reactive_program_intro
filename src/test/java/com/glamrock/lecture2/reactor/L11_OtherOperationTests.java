package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class L11_OtherOperationTests {
    @Test
    public void operationTest() {
        var f1 = Flux.range(1, 10);
        var f2 = Flux.range(11, 20);
        var f3 = Flux.range(21,30);

        Flux.zip(f1, f2, f3)
                .map(tuple -> tuple.getT1() + tuple.getT2() + tuple.getT3());

        Flux.firstWithSignal(f1, f2, f3);

        f1.then();

        Mono.justOrEmpty(1);

        Mono<Integer> mono = Mono.just(1);
        mono.defaultIfEmpty(2);
    }
}
