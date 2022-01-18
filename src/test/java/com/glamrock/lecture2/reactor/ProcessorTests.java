package com.glamrock.lecture2.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.*;

public class ProcessorTests {
    @Test
    void processorTest1() {
        List<String> array = Arrays.asList("a", "b", "c", "d", "e", "e");

        // array에 데이터가 새로 추가되면 subscribe는 과연 동작을 할 것 인가?
        Flux.fromIterable(array)
                .collectList()
                .subscribe(System.out::println);

        // 해당 구독행위는 array에 대해서 하였기 때문에 뒤에서 변화가 이루어진 부분에 대해서는 이미 끝난 행위라 아무런 변화가 없었다
        array.addAll(Arrays.asList("1", "2", "3"));
        /*
        그러면 한번 구독한 행위를 계속해서 발생하게 하려면 뭘 해야 되는 것 일까?
        subscribe의 내용을 계속해서 동작하게 하려면 어떻게 해야되는 것 일까?
         */
    }

    @Test
    void emitterTest1() {
        List<String> array = new ArrayList<>();
        array.addAll(Arrays.asList("a", "b", "c", "d", "e", "e"));

        // array에 데이터가 새로 추가되면 subscribe는 과연 동작을 할 것 인가?
        Flux.fromIterable(array)
                .collectList()
                .subscribe(System.out::println);

        array.addAll(Arrays.asList("1", "2", "3"));

        //프로세서 시작 구간.
        EmitterProcessor<List<String>> data = EmitterProcessor.create();
        data.subscribe(System.out::println);  //구독자 등장
        FluxSink<List<String>> sink = data.sink();   //발행인 등장
        sink.next(array);  //구독자에게 발행
    }

    @Test
    void emitterTest2() {
        List<String> array = new ArrayList<String>();
        array.addAll(Arrays.asList("a", "b", "c", "d", "e", "e"));

        /*
            EmitterProcessor는 신문사 이다.
            EmitterProcessor를 subscribe 하는 행위는 신문을 읽는 구독자(subscriber) 이다.
            그리고 FluxSink라는 클래스를 통해서 next 메소드를 호출하는 것은 신문을 발행하는 일이다.
            next를 통해서 신문을 계속해서 발행하면 subscribe를 통해서 정의한 내용이 계속해서 동작을 하게 된다.
         */
        //프로세서 시작 구간.
        EmitterProcessor<List<String>> data = EmitterProcessor.create();  //신문사
        data.subscribe(t -> System.out.println("101호 : " + t));  //신문을 읽는 101호 아저씨
        FluxSink<List<String>> sink = data.sink();   //신문 배달부
        sink.next(array); //신문 배달 완료

        array.addAll(Arrays.asList("new", "data", "hello"));  //신문 내용을 바꿈

        data.subscribe(t -> System.out.println("302호 : "+t));    //새롭게 돈을 내고 신문을 읽는 302호 아저씨 등장!
        sink.next(array);  //신문 배달

        array.addAll(Arrays.asList("1", "2", "3"));  //신문 내용을 바꿈
        sink.next(array);  //신문 배달

        /*
            Flux.from().subsrcibe() 과 같은 형태는
            데이터를 유지(keep)한 상태에서 구독자(subscriber)가 도착한 경우 해당 데이터를 전달하는 방식

            그런데 Processor들은 기존에 있거나 새롭게 등장한 구독자(subscriber)에게 데이터(old data)를 전달한다.
            발행하는 기관을 건설하고 구독자를 모집한 뒤에 계속해서 발행하는 형태이다.
            구독자에게 발행(next)을 하여 행위를 동작시키는 것이 주 목적이다.

            작업 할 내용이 데이터가 중심이면 일반 구독형태를 사용하고, 구독자가 중심이면 프로세스를 사용

            해당내용을 좀더 찾아보려면 hot publisher와 cold publisher라는 내용으로 검색을 해서 알아볼 수 있다.
         */
    }
}
