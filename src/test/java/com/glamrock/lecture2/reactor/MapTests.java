package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.GsonBuilderUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

public class MapTests {
    @Test
    void mapTest1() {
        List<String> array = Arrays.asList("a", "b", "c", "d", "e", "e");

        //해당 데이터를 구독!
        Flux.fromIterable(array)
                .groupBy(arr -> arr)
                .map(arg -> {  //첫번째 map, arg는 GroupedFlux
                    Mono<Map<Object, Object>> mono = arg.count()
                            .map(count -> { //두번째 map, arg.count()는 Mono<Long>
                                Map<Object, Object> item = new HashMap<>();
                                item.put(arg.key(), count);
                                return item;
                            });
                    return mono;  //첫번째 map 값을 Mono<Map<Object, Object>>로 바꾼다음,
                }).subscribe(System.out::println); // MonoMapFuseable 이 print
    }

    @Test
    void mapTest2() {
        List<String> array = Arrays.asList("a", "b", "c", "d", "e", "e");

        Flux.fromIterable(array)
                .groupBy(arr -> arr)
                .map(arg -> {  //첫번째 map, arg는 GroupedFlux
                    Mono<Map<Object, Object>> mono = arg.count()
                            .map(count -> { //두번째 map, arg.count()는 Mono<Long>
                                Map<Object, Object> item = new HashMap<>();
                                item.put(arg.key(), count);
                                return item;
                            });
                    return mono;  //첫번째 map 값을 Mono<Map<Object, Object>>로 바꾼다음,
                }).subscribe(data -> {
                    data.subscribe(System.out::println); // 결과가 mono 라 subscribe 를 해야 결과가 print 됨
                });
    }

    @Test
    void flatMapTest() {
        List<String> array = Arrays.asList("a", "b", "c", "d", "e", "e");

        Flux.fromIterable(array)
                .groupBy(arr -> arr)
                .flatMap(arg -> {  //첫번째 map, arg는 GroupedFlux
                    Mono<Map<Object, Object>> mono = arg.count()
                            .map(count -> { //두번째 map, arg.count()는 Mono<Long>
                                Map<Object, Object> item = new HashMap<>();
                                item.put(arg.key(), count);
                                return item;
                            });
                    return mono;  //첫번째 map 값을 Mono<Map<Object, Object>>로 바꾼다음,
                }).subscribe(System.out::println);
    }

    @Test
    void returnOfSubscribeTest() {
        List<String> array = Arrays.asList("a", "b", "c", "d", "e", "e");

        Disposable dispose = Flux.fromIterable(array)
                .groupBy(arr -> arr)
                .flatMap(arg -> {  //첫번째 map, arg는 GroupedFlux
                    Mono<Map<Object, Object>> mono = arg.count()
                            .map(count -> { //두번째 map, arg.count()는 Mono<Long>
                                Map<Object, Object> item = new HashMap<>();
                                item.put(arg.key(), count);
                                return item;
                            });
                    return mono;  //첫번째 map 값을 Mono<Map<Object, Object>>로 바꾼다음,
                }).subscribe(System.out::println);

        System.out.println("처분? -> " + dispose.isDisposed());
    }

    @Test
    void disPoseTest() {
        List<String> array = Arrays.asList("a", "b", "c", "d", "e", "e");

        Disposable dispose = Flux.fromIterable(array)
                .groupBy(arr -> arr)
                .flatMap(arg -> {  //첫번째 map, arg는 GroupedFlux
                    Mono<Map<Object, Object>> mono = arg.count()
                            .map(count -> { //두번째 map, arg.count()는 Mono<Long>
                                Map<Object, Object> item = new HashMap<>();
                                item.put(arg.key(), count);
                                return item;
                            });
                    return mono;  //첫번째 map 값을 Mono<Map<Object, Object>>로 바꾼다음,
                }).delayElements(Duration.ofSeconds(3)) // 3초 딜레이
                .collectList()
                .subscribe(System.out::println);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
        구독에 딜레이를 부여해서 구독을 중단
        위 내용을 실행하면 subscribe에 아무것도 나타나지 않는다.
         */
        System.out.println("처분? -> " + dispose.isDisposed());
        dispose.dispose();	 //중단!
        System.out.println("처분? -> " + dispose.isDisposed());
    }
}
