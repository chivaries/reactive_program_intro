package com.glamrock.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
public class RequestResponseExample {
    public static void main(String[] args) {

        RSocketServer.create(
                        SocketAcceptor.forRequestResponse(
                                p -> {
                                    String data = p.getDataUtf8();
                                    log.info("Received request data : {}", data);

                                    Payload responsePayload = DefaultPayload.create("Echo - " + data);
                                    p.release();

                                    return Mono.just(responsePayload);
                                }))
                .bind(TcpServerTransport.create("localhost", 7000))
                .delaySubscription(Duration.ofSeconds(5))
                .doOnNext(cc -> log.info("Server started on the address : {}", cc.address()))
                .block();

        Mono<RSocket> source =
                RSocketConnector.create()
                        .reconnect(Retry.backoff(50, Duration.ofMillis(500)))
                        .connect(TcpClientTransport.create("localhost", 7000));

        RSocketClient.from(source)
                .requestResponse(Mono.just(DefaultPayload.create("Test Request")))
                .doOnSubscribe(s -> log.info("Executing Request"))
                .doOnNext(
                        d -> {
                            log.info("Received response data : {}", d.getDataUtf8());
                            d.release();
                        })
                .repeat(2)
                .blockLast();
    }
}
