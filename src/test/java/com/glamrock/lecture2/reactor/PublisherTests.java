package com.glamrock.lecture2.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class PublisherTests {
    /*
    Mono나 Flux에서 사용되는 Publisher<T>인터페이스는 아래의 규칙을 따른다.

    1. Publisher 인터페이스를 오버라이드한 subscribe 메소드는 시작을 의미한다. (stream의 시작)
       여기서부터 어떠한 행위의 시작이 된다고 생각하면 될 것 같다.

    2. subscribe 메소드가 받는 클래스는 Subscriber 클래스이다.
       해당 클래스는 사용자가 콜백 형식으로 정의한 인터페이스 의미한다.
       해당 인터페이스에 onNext 메소드로 데이터 또는 이벤트를 전달하거나 onComplete로 종료를 알릴 수 있다.
       또한 onError 메소드를 통해서 에러를 전달 할 수 있다.

    3. Subscription 인터페이스는 이러한 사용자에게 전달할 행위를 정리해 둘 도화지 같은, 마치 도면같은 인터페이스이다.

    더욱 요약하면!

    1. Publisher는 구독할 대상이다.
    2. Subscriber는 사용자에게 전달할 대상이다.
    3. Subscription는 사용자에게 전달할 내용을 정리한 도화지이다.`
     */
    private static Publisher<String> publisher = new Publisher<String>() {  //퍼블리셔..?
        @Override
        public void subscribe(Subscriber<? super String> sbs) {
            Subscription subscript = new Subscription() {  //신청..?
                @Override
                public void request(long n) {
                    sbs.onNext("abcd");
                    sbs.onNext("EFgh");
                    sbs.onComplete();
                    System.out.println("내용동작");
                }
                @Override
                public void cancel() {

                }
            };
            System.out.println("시작");
            // 사용자의 콜백에 대한 행위 넣기
            sbs.onSubscribe(subscript);
            System.out.println("종료");
        }
    };

    @Test
    public void publisherTest1() {
        Mono.from(publisher)
                .map(arg -> arg.toUpperCase())
                .subscribe(arg -> System.out.println("Mono :" + arg));
    }

    @Test
    public void publisherTest2() {
        Flux.from(publisher)
                .map(arg -> arg.toUpperCase())
                .subscribe(arg -> System.out.println("Flux :" + arg));
    }

}
