package com.glamrock.lecture3.flux_subscribe;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class FluxSubscribeTests {
    @Test
    void test_flux_subscribe() {
        /*
        Flux.subscribe()
         Flux 에서 제공하는 팩토리 메서드는 subscriber 를 등록해주는 메서드도 있는 반면에, subscriber 를 매개변수로 등록하지 않는 함수도 존재한다.
         subscriber 가 필요없어서가 아니라, 내부 로직에서 자동으로 subscriber 를 만들어 준다.
         개발자가 따로 subscriber 를 등록하지 않아도 된다.
         */
        Flux<String> flux = Flux.just("A");
        flux.subscribe();
    }

    @Test
    void test_flux_subscribe_consumer() {
        /*
         Flux.subscribe(Consumer<? super T> consumer)
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
    void test_flux_subscribe_complete()  {
        /*
        Flux.subscribe(consumer, errorConsumer, completeConsumer)
        subscriber 의 onComplete 메서드를 정의할려면, Runnable 함수를 매개변수로 정의해서 넘겨줘야 한다.
         */
        Flux<String> flux = Flux.fromIterable(Arrays.asList("foo", "bar", "hello")).log();

        flux.subscribe(
                System.out::println,
                error -> {},
                () -> {
                    System.out.println("completeConsumer!!");
                }
        );
    }

    @Test
    void test_flux_subscribe_scription()  {
        /*
        Flux.subscribe(consumer, errorConsumer, completeConsumer, subscriptionConsumer)
        위에 작성한 테스트는, request(unbounded) 를 호출한다. unbounded 는 내부 로직에서 MAX 값을 설정하게 될 것이다.
        즉, Back-Pressure 기능을 활용하지 않고, 모든 데이터를 전송해 달라고 요청하는 것이다.
        Back-Pressure 를 사용하기 위해서 request(N) 에서 N 개수를 변경하고 싶다면,
        아래 메서드를 호출하면서 파라미터에 subscriptionConsumer 를 넘겨줘야 한다.
         */
        Flux<String> flux = Flux.fromIterable(Arrays.asList("foo", "bar", "hello")).log();

        // request(1) 를 호출하였기 때문에, 한개의 시퀀스(데이터)만 전달하고 대기하게 되는 상황이다.
        // 만약 이 상황에서 추가 데이터를 받아야 한다면 request를 다시 요청해야 한다.
        flux.subscribe(
                System.out::println,
                null,
                null,
                subscription -> subscription.request(1)
        );
    }

    @Test
    public void test_flux_custom_subscriber() {

        List<Integer> integerList = new ArrayList<>();

        /*
        피보탈의 Reactor 팀에서는 이런 방식을 추천하지 않는다. BaseSubscriber 클래스를 사용하는 것을 권장한다.
         */
        Flux.just(1, 2, 3, 4)
                .log()
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        integerList.add(integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        //TODO: request(1) 인 경우, Complete 가 되지 않은 상황에서 테스트가 통과하는것은 잘못된 테스트인듯.. 검토해보자.
                        //TODO: 결국 Reactor 에서 제공하는 StepVerifier 와 같은 Test 코드를 사용하는게 좋을 듯
                        assertThat(integerList.size()).isEqualTo(4);
                    }
                });

    }

    @Test
    public void test_flux_custom_baseSubscriber() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Flux<String> flux = Flux.just("에디킴", "아이린", "아이유", "수지");
        List<String> names = new ArrayList<>();

        flux.subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
                //super.hookOnSubscribe(subscription);
                //subscription.request(Long.MAX_VALUE)
            }

            @Override
            protected void hookOnNext(String value) {
                names.add(value);
                System.out.println(value);
                super.hookOnNext(value);
                request(1);
            }

            @Override
            protected void hookOnComplete() {
                assertThat(names.size()).isEqualTo(4);
                assertThat(names.get(0)).isEqualTo("에디킴");
                assertThat(names.get(3)).isEqualTo("수지");
                super.hookOnComplete();
            }
        });

        latch.await(1000, TimeUnit.MILLISECONDS);
    }
}
