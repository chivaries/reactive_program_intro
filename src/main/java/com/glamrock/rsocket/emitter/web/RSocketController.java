package com.glamrock.rsocket.emitter.web;

import com.glamrock.rsocket.emitter.model.Item;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_ROUTING;
import static org.springframework.http.MediaType.*;

@RestController
public class RSocketController {
    /*
        RSocketRequester 를 사용해야 spring framework 과 연동
        도착지를 기준으로 메시지를 라우팅 할 수 있음
        보너스로 트래픽의 인코딩/디코딩도 쉽게 할 수 있음
        RSocketRequester 를 사용하지 않으면 클라이언트와 서버 양쪽의 모든 R소켓 연결에서 데이터를 직접 관리해야함

        왜 Mono 로 감싸는가?
        리액터의 Mono 패러다임은 connection 을 R소켓 연결 세부 정보를 포함하는 지연 구조체 (lazy construct) 로 전환한다.
        즉 아무도 연결하지 않으면 R소켓은 열리지 않는다. 누군가가 구독해야 세부 정보가 여러 구독자에 공유될 수 있다.

        하나의 R소켓만으로 모든 구독자에게 서비스 할 수 있다.
        R소켓을 구독자마다 1개씩 만들 필요가 없다. 대신에 하나의 R소켓 파이프에 대해 구독자별로 하나씩 연결을 생성한다.
    */
    private final Mono<RSocketRequester> requester;

    /*
    spring boot 가 RSocketRequestAutoConfiguration 정책 안에서 자동설정으로 RSocketRequester.Builder 빈을 만들어 줌
     */
    public RSocketController(RSocketRequester.Builder builder) {
        this.requester = builder
                .dataMimeType(APPLICATION_JSON)
                .metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString()))
                .connectTcp("localhost", 7000)
                .retry(5)
                .cache(); // 요청 Mono 를 hot source 로 전환. 최근의 신호는 캐시될 있을 수도 있고 구독자는 사본을 가질 수도 있다.
                          // 다수의 클라이언트가 동일한 하나의 데이터를 요구할 때 효율성을 높일 수 있음
    }

    @PostMapping("/items/request-response")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item) {
        /*
        spring webfux 와 R소켓 API 가 모드 프로젝트 리액터를 사용하는 덕분에 매끄럽게 연결된다.
        하나의 flow 안에서 chaining 으로 연결되어 http 웹 요청을 R소켓 연결에 전달하고 응답을 받아 클라이언트에 리액티브하게 반환 할 수 있다.
         */
        return this.requester
                .flatMap(rSocketRequester -> rSocketRequester
                        .route("newItems.request-response")
                        .data(item)
                        .retrieveMono(Item.class))
                .map(savedItem ->
                        ResponseEntity.created(URI.create("/items/request-response")).body(savedItem)
                );
    }

    @GetMapping(value = "/items/request-stream", produces = APPLICATION_NDJSON_VALUE)
    Flux<Item> findItemUsingRSocketRequestStream() {
        return this.requester
                .flatMapMany(rSocketRequester -> rSocketRequester
                        .route("newItems.request-stream")
                        .retrieveFlux(Item.class)
                        .delayElements(Duration.ofSeconds(1))
                );
    }

    @PostMapping("/items/fire-and-forget")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketFireAndForget(@RequestBody Item item) {
        return this.requester
                .flatMap(rSocketRequester -> rSocketRequester
                        .route("newItems.fire-and-forget")
                        .data(item)
                        .send())
                /*
                    앞의 예제에서는 새 Item 정보가 포함된 Mono 를 map 을 통해 Item 정보를 포함하는 ResponseEntity 를 Mono 로 변환하여 반환하였지만
                    이 예제에서는 Mono<Void> 를 반환받았으므로 map() 을 한다 해도 아무 일도 일어나지 않는다.
                    따라서 then() 을 사용하여 Mono 를 새로 만들어서 반환한다.
                 */
                .then(
                        Mono.just(ResponseEntity.created(URI.create("/items/fire-and-forget")).build())
                );
    }

    /*
        스트림 응답을 받으면 전체 결과를 모두 가져 올 수 있을 때까지 기다렸다가 실행을 종료하는 방식으로 동작하지 않는다.
        결과값이 생길 때마다 결과를 화면에 표시하고 실행을 종료하지 않고 응답을 받을 수 있는 대기 상태로 남는다.
     */
    @GetMapping(value = "/items", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Item> liveUpdate() {
        return this.requester
                .flatMapMany(rSocketRequester -> rSocketRequester
                        .route("newItems.monitor")
                        .retrieveFlux(Item.class));
    }
}
